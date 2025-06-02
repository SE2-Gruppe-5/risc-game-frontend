package com.se2gruppe5.risikofrontend.game.dialogues

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.se2gruppe5.risikofrontend.databinding.DialogTradeCardsBinding
import com.se2gruppe5.risikofrontend.game.cards.CardHandler
import com.se2gruppe5.risikofrontend.game.dataclasses.CardRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.enums.CardType

class TradeCardDialog(
    context: Context,
    player: PlayerRecord,
    mustTrade: Boolean,
) : AlertDialog(context) {

    private val binding: DialogTradeCardsBinding = DialogTradeCardsBinding.inflate(LayoutInflater.from(context))
    private var forced: Boolean = false
    private var p: PlayerRecord? = null
    private var c: Context? = null
    var infantry = 0
    var cavalry = 0
    var artillery = 0

    init {
        setView(binding.root)
        forced = mustTrade
        p = player
        c = context
        val cards = p!!.cards
        for(card in cards){
            if (card.type == CardType.Infantry) infantry++
            if (card.type == CardType.Cavalry) cavalry++
            if (card.type == CardType.Artillery) artillery++
        }

        binding.type1Input.hint = "You have $infantry type 1 cards"
        binding.type2Input.hint = "You have $cavalry type 2 cards"
        binding.type3Input.hint = "You have $artillery type 3 cards"


        setButton(BUTTON_POSITIVE, "OK") { _, _ ->
            handlePositiveButton()
        }
        setButton(BUTTON_NEGATIVE, "Cancel") { _, _ ->
            dismiss()
        }
        setTitle("Select how many cards you want to trade (3 cards in total)")
    }

    private fun handlePositiveButton(){
        var type1 = 0
        var type2 = 0
        var type3 = 0
        try{
        type1 = binding.type1Input.text.toString().toInt()
        type2 = binding.type2Input.text.toString().toInt()
        type3 = binding.type3Input.text.toString().toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Invalid number", Toast.LENGTH_SHORT).show()
        }
        val amount = type3+type2+type1
        if(amount !=3){
            Toast.makeText(context, "You have to trade 3 cards", Toast.LENGTH_SHORT).show()
            if(this.forced){
                TradeCardDialog(c!!,p!!,forced)
            }
        }else{
            if(type1 > infantry || type2 > cavalry || type3 > artillery){
                Toast.makeText(context, "You do not own these cards", Toast.LENGTH_SHORT).show()
                if(this.forced){
                    TradeCardDialog(c!!,p!!,forced)
                }
            }
            val cards = mutableListOf<CardRecord>()
            while(type1 >0){
                type1 -= 1
                cards.add(CardRecord(CardType.Infantry))
            }
            while(type2 >0){
                type2 -= 1
                cards.add(CardRecord(CardType.Cavalry))
            }
            while(type3 >0){
                type3 -= 1
                cards.add(CardRecord(CardType.Artillery))
            }
            val troops = tradeAction(cards)
            if(troops == -1){
                Toast.makeText(context, "Enter a correct Selection of cards", Toast.LENGTH_SHORT).show()
                if(this.forced){
                    TradeCardDialog(c!!,p!!,forced)
                }
            }else{
                Toast.makeText(context, "Succesfully got $troops troops", Toast.LENGTH_SHORT).show()
            }
        }



}

    private fun tradeAction(cards: MutableList<CardRecord>): Int {
        var troops = CardHandler.tradeCards(cards)
        if(troops == -1) return troops
        var playerCards = p!!.cards
        for(card in cards){
            for(pCard in playerCards){
                if(card.type == pCard.type){
                    playerCards.remove(pCard)
                    break
                }
            }
        }
        p!!.cards = playerCards
        p!!.freeTroops = p!!.freeTroops + troops
        return troops;
    }
}

