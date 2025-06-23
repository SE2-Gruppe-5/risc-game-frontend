package com.se2gruppe5.risikofrontend.game

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.se2gruppe5.risikofrontend.game.popup.ContinentDialog
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.dice.DiceVisualAndroid
import com.se2gruppe5.risikofrontend.game.dice.diceModels.Dice1d6Generic
import com.se2gruppe5.risikofrontend.game.enums.Phases
import com.se2gruppe5.risikofrontend.game.managers.GameManager
import com.se2gruppe5.risikofrontend.network.NetworkClient
import com.se2gruppe5.risikofrontend.network.sse.MessageType
import com.se2gruppe5.risikofrontend.network.sse.SseClientService
import com.se2gruppe5.risikofrontend.network.sse.constructServiceConnection
import com.se2gruppe5.risikofrontend.network.sse.messages.ChangeTerritoryMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.UpdatePhaseMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.UpdatePlayersMessage
import kotlinx.coroutines.runBlocking
import java.util.UUID
import android.util.Log
import androidx.transition.Visibility
import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dialogues.DialogueHandler
import com.se2gruppe5.risikofrontend.game.hardware.FlashLightHardwareAndroid
import com.se2gruppe5.risikofrontend.game.hardware.IFlashLightHardware
import com.se2gruppe5.risikofrontend.game.hardware.IShakeHardware
import com.se2gruppe5.risikofrontend.game.hardware.ShakeHardwareAndroid
import com.se2gruppe5.risikofrontend.game.popup.ShakePhoneAlert
import com.se2gruppe5.risikofrontend.game.managers.GameViewManager
import com.se2gruppe5.risikofrontend.game.managers.TerritoryManager
import com.se2gruppe5.risikofrontend.game.managers.ToastUtilAndroid
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid
import com.se2gruppe5.risikofrontend.network.sse.messages.GameStartMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.PlayerWonMessage
import org.w3c.dom.Text
import java.io.Serializable


class GameActivity : AppCompatActivity() {
    val client = NetworkClient()
    var sseService: SseClientService? = null
    var nextPhaseBtn: Button? = null
    var reinforceIndicator: TextView? = null
    var attackIndicator: TextView? = null
    var tradeIndicator: TextView? = null
    var phaseTxt: TextView? = null
    var viewManager: GameViewManager? = null

    var turnIndicators: MutableList<TextView> = mutableListOf()
    var gameManager: GameManager? = null
    var gameID: UUID? = null
    var me: PlayerRecord? = null
    var troopText: TextView? = null
    val serviceConnection = constructServiceConnection { service ->
        // Allow network calls on main thread for testing purposes
        // GitHub Actions Android emulator action with stricter policy fails otherwise
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )
        sseService = service
        if (service != null) {
            gameManager = GameManager.get()
            setupHandlers(service)
            getGameInfo()

        }


    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        enableEdgeToEdge()
        setContentView(R.layout.game)


        val gameStart =
            getSerializableExtraCompat(intent, "GAME_DATA", GameStartMessage::class.java)!!
         me = getSerializableExtraCompat(intent, "LOCAL_PLAYER", PlayerRecord::class.java)!!
        val dialogHandler = DialogueHandler(this)
        gameID = gameStart.gameId

        TerritoryManager.init(
            me!!,
            PointingArrowAndroid(this),
            ToastUtilAndroid(this),
            dialogHandler
        )
        GameManager.init(me!!, gameID!!, TerritoryManager.get(), client, gameStart.players)
        setupDiceInteractions()

        turnIndicators.add(this.findViewById<TextView>(R.id.player1txt))
        turnIndicators.add(this.findViewById<TextView>(R.id.player2txt))
        turnIndicators.add(this.findViewById<TextView>(R.id.player3txt))
        turnIndicators.add(this.findViewById<TextView>(R.id.player4txt))
        turnIndicators.add(this.findViewById<TextView>(R.id.player5txt))
        turnIndicators.add(this.findViewById<TextView>(R.id.player6txt))

        nextPhaseBtn = this.findViewById<Button>(R.id.phaseBtn)
        reinforceIndicator = this.findViewById<TextView>(R.id.reinforceIndicator)
        attackIndicator = this.findViewById<TextView>(R.id.attackIndicator)
        tradeIndicator = this.findViewById<TextView>(R.id.tradeIndicator)
        phaseTxt = this.findViewById<TextView>(R.id.currentPhaseTxt)
        troopText = this.findViewById<TextView>(R.id.freeTroopTxt)

        viewManager = GameViewManager(this)
        viewManager?.initializeGame(this, turnIndicators)

        this.findViewById<TextView>(R.id.txtWonMessage).visibility = View.INVISIBLE

        val tradeCardButton = this.findViewById<Button>(R.id.tradeCardButton)

        tradeCardButton.setOnClickListener {
            if(me!!.cards.size>=3){
                dialogHandler.useTradeCardDialog(me!!, false)

                viewManager?.updateCardDisplay(me!!)
            }else{
                Toast.makeText(this@GameActivity, "You do not have enough cards to Trade", Toast.LENGTH_SHORT).show()
            }
        }
        nextPhaseBtn?.setOnClickListener {
            changePhase()
            if(me!!.cards.size == 5){
                dialogHandler.useTradeCardDialog(me!!, true)
            }
            viewManager?.updateCardDisplay(me!!)
            updateFreeTroops()
            Log.i("GameManger", gameID.toString())
        }
        val showContinentButton: Button = this.findViewById(R.id.btn_show_continents)
        showContinentButton.setOnClickListener {
            showContinentDialog()
        }

    }

    private fun updateFreeTroops(){
      troopText!!.text = me!!.freeTroops.toString()
    }

    override fun onStart() {
        super.onStart()
        Intent(this, SseClientService::class.java).also {
            bindService(it, serviceConnection, BIND_AUTO_CREATE)
        }
    }


    override fun onStop() {
        super.onStop()
        GameManager.reset()
        TerritoryManager.reset()
        if (sseService != null) {
            unbindService(serviceConnection)
        }
    }

    private fun changePhase() {
        runBlocking {
            if (!GameManager.get().nextPhase()) {
                Toast.makeText(this@GameActivity, "It's not your turn", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getGameInfo() {
        Log.i("GameManager", gameID.toString())
        runBlocking {
            client.getGameInfo(gameID!!)
        }
    }

    val highlightedColor = Color.BLACK
    val notHighlightedColor = Color.GRAY

    private fun changeViewColor(view: TextView?, highlightPhase: Phases, currentPhase: Phases) {
        view?.setBackgroundColor(if (currentPhase == highlightPhase) highlightedColor else notHighlightedColor)
    }

    private fun changeViewColors(phase: Phases) {
        changeViewColor(reinforceIndicator, Phases.Reinforce, phase)
        changeViewColor(attackIndicator, Phases.Attack, phase)
        changeViewColor(tradeIndicator, Phases.Trade, phase)
    }

    fun showContinentDialog() {
        val dialog = ContinentDialog()
        dialog.show(supportFragmentManager, "ContinentDialog")
    }

    private fun changeHighlightedPlayer(number: Int?, indicators: List<View>) {
        for (i in indicators.indices) {
            if (i == number) {
                indicators[i].background =
                    ContextCompat.getDrawable(this, R.drawable.higlightedbackground)
            } else {
                indicators[i].background =
                    ContextCompat.getDrawable(this, R.drawable.nothiglightedbackground)
            }
        }

    }


    private fun setupHandlers(service: SseClientService) {
        sseService?.handler(MessageType.UPDATE_PHASE) {
            it as UpdatePhaseMessage
            val phase: Phases = Phases.entries[it.phase]
            GameManager.get().receivePhaseUpdate(phase)
            changeViewColors(phase)
            phaseTxt?.text = phase.toString()
            nextPhaseBtn?.text = when (phase) {
                Phases.Trade -> "End Turn"
                else -> "Next Phase"
            }
        }
        sseService?.handler(MessageType.UPDATE_PLAYERS) {
            it as UpdatePlayersMessage
            GameManager.get().receivePlayerListUpdate(it.players)
            for (player in it.players) {
                Log.i(
                    "GameManger",
                    "${player.value.id} ${player.key} ${player.value.isCurrentTurn}"
                )
            }
            val currentPlayerIndex = it.players.values.indexOfFirst { it.isCurrentTurn }
            changeHighlightedPlayer(currentPlayerIndex, turnIndicators)
        }
        sseService?.handler(MessageType.UPDATE_TERRITORIES) {
            it as ChangeTerritoryMessage
            GameManager.get().getTerritoryManager().updateTerritories(it.territories)

        }
        sseService?.handler(MessageType.PLAYER_WON){
            it as PlayerWonMessage
            displayWinner(gameManager!!.getPlayer(it.winner)!!.name)
        }


    }

    private fun displayWinner(s: String) {
    runOnUiThread {
        var msg = s + "Won the Game!!"
        var wonMessage = this.findViewById<TextView>(R.id.txtWonMessage)
        wonMessage.text = msg
        wonMessage.visibility = View.VISIBLE
    }
    }

    /**
    Workaround for using getSerializableExtra on all Android versions
     **/
    private fun <T : Serializable> getSerializableExtraCompat(
        intent: Intent,
        varName: String,
        retClass: Class<T>
    ): T? {
        if (Build.VERSION.SDK_INT >= 33) {
            return intent.getSerializableExtra(varName, retClass)
        }

        @Suppress("DEPRECATION", "UNCHECKED_CAST")
        return intent.getSerializableExtra(varName) as? T
    }

    private fun setupDiceInteractions(){
        val shakeHW = ShakeHardwareAndroid.getInstance(this)
        val flashHW = FlashLightHardwareAndroid.getInstance(this)
        // - Dice UI/UX -
        val diceBtn = this.findViewById<ImageButton>(R.id.diceButton)
        val diceTxt = this.findViewById<TextView>(R.id.diceText)
        val shakePhoneAlert = ShakePhoneAlert(this)
        val diceVisualAndroid =
            DiceVisualAndroid(Dice1d6Generic(), diceBtn, diceTxt, shakeHW, shakePhoneAlert)
        //Wire up lambda interactions
        diceVisualAndroid.clickSubscription { it.hwInteraction() }
        shakePhoneAlert.registerLambda = { shakeHW.sensorRegisterListener() }
        shakePhoneAlert.deregisterLambda = {
            shakeHW.sensorDeRegisterListener()
            diceVisualAndroid.resetDice()
        }
        shakePhoneAlert.setCheatLambda = { dice ->
            shakeHW.sensorDeRegisterListener()
            diceVisualAndroid.setDice(dice)
            diceVisualAndroid.roll()
            flashHW.blink() //Make Phone's Camera Flash-Light blink when cheating ...
            diceVisualAndroid.resetDice()
            // By Design i have chosen to let the two cheating variants
            // be performed without shaking the phone.
            // (More fun to spot someone cheating when playing in person this way)
        }
    }

}





