package com.se2gruppe5.risikofrontend.game.dialogues

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.se2gruppe5.risikofrontend.databinding.DialogTradeCardsBinding
import com.se2gruppe5.risikofrontend.game.cards.CardHandler
import com.se2gruppe5.risikofrontend.game.dataclasses.game.CardRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.game.enums.CardType
import java.util.EnumMap

class TradeCardDialog(
    context: Context,
    player: PlayerRecord,
    mustTrade: Boolean,
) : AlertDialog(context) { //todo (maybe) change territory-binding hardcoded values

    private val binding: DialogTradeCardsBinding =
        DialogTradeCardsBinding.inflate(LayoutInflater.from(context))
    private var forced: Boolean = false
    private var playerRecord: PlayerRecord? = null
    private var ctx: Context? = null
    val cardCountMap = EnumMap<CardType, Int>(CardType::class.java)


    init {
        setView(binding.root)
        forced = mustTrade
        playerRecord = player
        ctx = context
        val playerCards = playerRecord!!.cards

        initEnum(cardCountMap)

        //Count the cards up
        for (card in playerCards) {
            cardCountMap[card.type] = (cardCountMap[card.type] ?: 0) + 1
        }

        //Set type hints
        binding.type1Input.hint = "You have ${cardCountMap[CardType.Infantry] ?: 0} Infantry cards"
        binding.type2Input.hint = "You have ${cardCountMap[CardType.Cavalry] ?: 0} Cavalry cards"
        binding.type3Input.hint =
            "You have ${cardCountMap[CardType.Artillery] ?: 0} Artillery cards"

        setButton(BUTTON_POSITIVE, "OK") { _, _ ->
            handlePositiveButton()
        }
        setButton(BUTTON_NEGATIVE, "Cancel") { _, _ ->
            dismiss()
        }
        setTitle("Select how many cards you want to trade (3 cards in total)")
    }

    private fun noDismissCheck() {
        if (this.forced) {
            TradeCardDialog(ctx!!, playerRecord!!, true)
        }
    }

    private fun checkTradeValid(inputCardCountMap: EnumMap<CardType, Int>): Boolean {

        if (inputCardCountMap.values.sum() != 3) {
            displayErrorMsg("You have to trade 3 cards!")
            return false
        }

        for ((cardType, count) in inputCardCountMap.entries) {
            val available = cardCountMap[cardType] ?: 0
            if (count > available) {
                displayErrorMsg("You do not own enough ${cardType.name.lowercase()} cards")
                return false
            }
        }
        return true
    }

    private fun issuesCards(inputCardCountMap: EnumMap<CardType, Int>): MutableList<CardRecord> {
        val cards = mutableListOf<CardRecord>()
        for ((cardType, count) in inputCardCountMap.entries) {
            repeat(count) {
                cards.add(CardRecord(cardType))
            }
        }
        return cards
    }

    private fun handlePositiveButton() {
        //Tally up binding-inputs
        val bindingRefList = listOf(
            binding.type1Input to CardType.Infantry,
            binding.type2Input to CardType.Cavalry,
            binding.type3Input to CardType.Artillery
        )
        val inputCardCountMap = EnumMap<CardType, Int>(CardType::class.java)
        initEnum(cardCountMap)

        for ((bindingRef, cardType) in bindingRefList) {
            val cardCount = bindingRef.text.toString().toIntOrNull()
            if (cardCount == null) {
                displayErrorMsg("Invalid number!")
                return
            }
            inputCardCountMap[cardType] = cardCount
        }

        if (checkTradeValid(inputCardCountMap)) {
            val troops = tradeAction(issuesCards(inputCardCountMap))
            if (troops == -1) {
                displayErrorMsg("Enter a correct Selection of cards")
            } else {
                displayInfoMsg("Successfully got $troops troops")
            }
        }
    }

    private fun displayErrorMsg(msg: String) {
        displayInfoMsg(msg)
        noDismissCheck()
    }

    private fun displayInfoMsg(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    private fun tradeAction(cards: MutableList<CardRecord>): Int {
        var troops = CardHandler.tradeCards(cards)
        if (troops == -1) return troops
        var playerCards = playerRecord!!.cards
        for (card in cards) {
            for (pCard in playerCards) {
                if (card.type == pCard.type) {
                    playerCards.remove(pCard)
                    break
                }
            }
        }
        playerRecord!!.cards = playerCards
        playerRecord!!.freeTroops = playerRecord!!.freeTroops + troops
        return troops;
    }

    //Init Enum w/ all zeroes
    private fun initEnum(enum: EnumMap<CardType, Int>) {
        for (cardType in CardType.entries) {
            enum[cardType] = 0
        }
    }

}

