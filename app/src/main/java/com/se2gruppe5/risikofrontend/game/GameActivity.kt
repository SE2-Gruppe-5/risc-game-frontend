package com.se2gruppe5.risikofrontend.game

import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dice.DiceVisualAndroid
import com.se2gruppe5.risikofrontend.game.dice.diceModels.Dice1d6Generic
import com.se2gruppe5.risikofrontend.game.enums.Phases
import com.se2gruppe5.risikofrontend.game.managers.GameManager
import com.se2gruppe5.risikofrontend.network.NetworkClient
import com.se2gruppe5.risikofrontend.network.sse.MessageType
import com.se2gruppe5.risikofrontend.network.sse.SseClientService
import com.se2gruppe5.risikofrontend.network.sse.constructServiceConnection
import com.se2gruppe5.risikofrontend.network.sse.messages.ChangeTerritoryMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.ChatMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.GameStartMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.JoinLobbyMessage
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
        }


    }
    var gameManager: GameManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.game)
        //Placeholder
        val diceBtn = this.findViewById<ImageButton>(R.id.diceButton)
        val diceTxt = this.findViewById<TextView>(R.id.diceText)
        val diceVisualAndroid = DiceVisualAndroid(Dice1d6Generic(), diceBtn, diceTxt)
        diceVisualAndroid.clickSubscription { it.roll() }

        val player1TurnIndicator = this.findViewById<TextView>(R.id.player1txt)
        val player2TurnIndicator = this.findViewById<TextView>(R.id.player2txt)
        val player3TurnIndicator = this.findViewById<TextView>(R.id.player3txt)
        val player4TurnIndicator = this.findViewById<TextView>(R.id.player4txt)
        val player5TurnIndicator = this.findViewById<TextView>(R.id.player5txt)
        val player6TurnIndicator = this.findViewById<TextView>(R.id.player6txt)
        val turnIndicators = listOf<TextView>( player1TurnIndicator, player2TurnIndicator, player3TurnIndicator, player4TurnIndicator, player5TurnIndicator, player6TurnIndicator)

        val nextPhaseBtn = this.findViewById<Button>(R.id.phaseBtn)
        val reinforceIndicator = this.findViewById<TextView>(R.id.reinforceIndicator)
        val attackIndicator = this.findViewById<TextView>(R.id.attackIndicator)
        val tradeIndicator = this.findViewById<TextView>(R.id.tradeIndicator)
        val phaseTxt = this.findViewById<TextView>(R.id.currentPhaseTxt)

        val gameManager = GameManager.get()
        gameManager.initializeGame(this, turnIndicators)
        nextPhaseBtn.setOnClickListener {
            var res = gameManager.nextPhase()
            var phase = res.first
            when(phase){
                Phases.Reinforce -> {changeViewColors(reinforceIndicator,attackIndicator,tradeIndicator)
                    phaseTxt.text = "Reinforce"
                    nextPhaseBtn.text = "Next Phase"
                    changeHighlightedPlayer(res.second,turnIndicators)
                }
                Phases.Attack -> {changeViewColors(attackIndicator,reinforceIndicator,tradeIndicator)
                    phaseTxt.text = "Attack"
                    nextPhaseBtn.text = "Next Phase"
                }
                Phases.Trade -> {changeViewColors(tradeIndicator,attackIndicator,reinforceIndicator)
                    phaseTxt.text = "Trade"
                    nextPhaseBtn.text = "End Turn"
                }

                Phases.OtherPlayer -> {
                    Toast.makeText(this, "Wait for your turn", Toast.LENGTH_SHORT).show()
                }
            }


        }

    }
    val highlightedColor = Color.BLACK
    val notHighlightedColor = Color.GRAY

    private fun changeViewColors(highlighted: View, not1: View, not2: View){
        highlighted.setBackgroundColor(highlightedColor)
        not1.setBackgroundColor(notHighlightedColor)
        not2.setBackgroundColor(notHighlightedColor)
    }
    private fun changeHighlightedPlayer(number: Int, indicators: List<View>){
        for(i in indicators.indices){
            if(i == number){
                indicators[i].background = ContextCompat.getDrawable(this, R.drawable.higlightedbackground)
            }else{
                indicators[i].background = ContextCompat.getDrawable(this, R.drawable.nothiglightedbackground)
            }
        }

    }


    private fun setupHandlers(service: SseClientService) {
            sseService?.handler(MessageType.UPDATE_PHASE) {
                it as UpdatePhaseMessage



            }
        sseService?.handler(MessageType.UPDATE_PLAYERS) {
            it as UpdatePlayersMessage



        }
        sseService?.handler(MessageType.UPDATE_TERRITORIES) {
            it as ChangeTerritoryMessage



        }



    }



}





