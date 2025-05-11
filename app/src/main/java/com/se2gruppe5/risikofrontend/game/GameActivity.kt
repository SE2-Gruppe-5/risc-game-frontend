package com.se2gruppe5.risikofrontend.game

import android.content.pm.ActivityInfo
import android.graphics.Color
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
        //Placeholder
        val diceBtn = this.findViewById<ImageButton>(R.id.diceButton)
        val diceTxt = this.findViewById<TextView>(R.id.diceText)
        val diceVisualAndroid = DiceVisualAndroid(Dice1d6Generic(), diceBtn, diceTxt)
        diceVisualAndroid.clickSubscription { it.roll() }
        gameID = UUID.fromString(intent.getStringExtra("GAME_ID"))
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

        val gameManager = GameManager.get()
        gameManager.initializeGame(this, turnIndicators)
        nextPhaseBtn?.setOnClickListener {
            changePhase()

        }
        val showContinentButton: Button = this.findViewById(R.id.btn_show_continents)
        showContinentButton.setOnClickListener {
            showContinentDialog()
        }

    }

    private fun changePhase() {
        runBlocking {
            client.changePhase(gameID!!)
        }
    }

    private fun getGameInfo() {
        runBlocking {
            client.getGameInfo(gameID!!)
        }
    }

    val highlightedColor = Color.BLACK
    val notHighlightedColor = Color.GRAY

    private fun changeViewColors(highlighted: TextView?, not1: TextView?, not2: TextView?) {
        highlighted?.setBackgroundColor(highlightedColor)
        not1?.setBackgroundColor(notHighlightedColor)
        not2?.setBackgroundColor(notHighlightedColor)

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
            var phase: Phases = Phases.Reinforce
            when (it.phase) {
                0 -> phase = Phases.Reinforce
                1 -> phase = Phases.Attack
                2 -> phase = Phases.Trade
            }
            val res = gameManager?.nextPhase(phase)
            when (phase) {
                Phases.Reinforce -> {
                    changeViewColors(reinforceIndicator, attackIndicator, tradeIndicator)
                    phaseTxt?.text = "Reinforce"
                    nextPhaseBtn?.text = "Next Phase"
                    changeHighlightedPlayer(res?.second, turnIndicators)
                }

                Phases.Attack -> {
                    changeViewColors(attackIndicator, reinforceIndicator, tradeIndicator)
                    phaseTxt?.text = "Attack"
                    nextPhaseBtn?.text = "Next Phase"
                }

                Phases.Trade -> {
                    changeViewColors(tradeIndicator, attackIndicator, reinforceIndicator)
                    phaseTxt?.text = "Trade"
                    nextPhaseBtn?.text = "End Turn"
                }

                Phases.OtherPlayer -> {
                    Toast.makeText(this, "Wait for your turn", Toast.LENGTH_SHORT).show()
                }
            }


        }
        sseService?.handler(MessageType.UPDATE_PLAYERS) {
            it as UpdatePlayersMessage
            var higlightedPlayer = gameManager?.updatePlayers(it.players)
            changeHighlightedPlayer(higlightedPlayer, turnIndicators)

        }
        sseService?.handler(MessageType.UPDATE_TERRITORIES) {
            it as ChangeTerritoryMessage
            gameManager!!.updateTerritories(it.territories)

        }

    }


}





