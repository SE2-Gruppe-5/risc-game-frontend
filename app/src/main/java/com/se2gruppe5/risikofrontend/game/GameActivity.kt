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
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dialogues.DialogueHandler
import com.se2gruppe5.risikofrontend.game.dice.diceHardware.DiceHardwareAndroid
import com.se2gruppe5.risikofrontend.game.dice.popups.ShakePhoneAlert
import com.se2gruppe5.risikofrontend.game.managers.GameViewManager
import com.se2gruppe5.risikofrontend.game.managers.TerritoryManager
import com.se2gruppe5.risikofrontend.game.managers.ToastUtilAndroid
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid
import com.se2gruppe5.risikofrontend.network.sse.messages.GameStartMessage
import java.io.Serializable


class GameActivity : AppCompatActivity() {
    val client = NetworkClient()
    var sseService: SseClientService? = null
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
    var nextPhaseBtn: Button? = null
    var reinforceIndicator: TextView? = null
    var attackIndicator: TextView? = null
    var tradeIndicator: TextView? = null
    var phaseTxt: TextView? = null

    var turnIndicators: MutableList<TextView> = mutableListOf()
    var gameManager: GameManager? = null
    var gameID: UUID? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        enableEdgeToEdge()
        setContentView(R.layout.game)

        val gameStart =
            getSerializableExtraCompat(intent, "GAME_DATA", GameStartMessage::class.java)!!
        val me = getSerializableExtraCompat(intent, "LOCAL_PLAYER", PlayerRecord::class.java)!!

        gameID = gameStart.gameId

        TerritoryManager.init(
            me,
            PointingArrowAndroid(this),
            ToastUtilAndroid(this),
            DialogueHandler(this)
        )
        GameManager.init(me, gameID!!, TerritoryManager.get(), client, gameStart.players)

        // - Dice UI/UX -
        val diceBtn = this.findViewById<ImageButton>(R.id.diceButton)
        val diceTxt = this.findViewById<TextView>(R.id.diceText)
        val diceHW = DiceHardwareAndroid(this)
        val shakePhoneAlert = ShakePhoneAlert(this)
        val diceVisualAndroid =
            DiceVisualAndroid(Dice1d6Generic(), diceBtn, diceTxt, diceHW, shakePhoneAlert)
        //Wire up lambda interactions
        diceVisualAndroid.clickSubscription { it.hwInteraction() }
        shakePhoneAlert.registerLambda = { diceHW.sensorRegisterListener() }
        shakePhoneAlert.deregisterLambda = {
            diceHW.sensorDeRegisterListener()
            diceVisualAndroid.resetDice()
        }
        shakePhoneAlert.setCheatLambda = { dice -> diceVisualAndroid.setDice(dice) }

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

        val viewManager = GameViewManager(this)
        viewManager.initializeGame(this, turnIndicators)

        nextPhaseBtn?.setOnClickListener {
            changePhase()
            Log.i("GameManger", gameID.toString())
        }
        val showContinentButton: Button = this.findViewById(R.id.btn_show_continents)
        showContinentButton.setOnClickListener {
            showContinentDialog()
        }

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


}





