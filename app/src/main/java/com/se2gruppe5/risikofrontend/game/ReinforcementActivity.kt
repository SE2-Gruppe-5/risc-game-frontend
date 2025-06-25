package com.se2gruppe5.risikofrontend.game

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.se2gruppe5.risikofrontend.R

class ReinforcementActivity : AppCompatActivity() {

    private lateinit var troopSelector: View
    private lateinit var troopNumbers: RecyclerView
    private lateinit var adapter: TroopNumberAdapter
    private lateinit var snapHelper: LinearSnapHelper

    private lateinit var btnDecline: ImageView
    private lateinit var btnAccept: ImageView


    private var troopCount = 10 // to change //todo ?
    private var deployedTroops = 0 // to change //todo ?

    private lateinit var scrollView: HorizontalScrollView
    private lateinit var container: LinearLayout
    private lateinit var btnShowCards: TextView
    private lateinit var btnTradeIn: Button
    private var cardValues: MutableList<Int> = mutableListOf(2, 3, 1, 2)
    private val selectedCards = mutableListOf<View>()
    private val selectedIndices = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.reinforcement)

        troopSelector = findViewById(R.id.troopSelector)
        findViewById<ImageButton>(R.id.territoryAbtn).setOnClickListener {
            troopSelector.visibility = View.VISIBLE
        }

        snapHelper = LinearSnapHelper().also {
            it.attachToRecyclerView(troopNumbers)
        }

        setupButtons()
        setupTroopCountDisplay()

    }

    private fun setupTroopCountDisplay() {
        troopNumbers = findViewById(R.id.rvTroopNumbers)
        troopNumbers.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = TroopNumberAdapter()
        troopNumbers.adapter = adapter
        troopNumbers.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                troopNumbers.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val totalPx = troopNumbers.width
                val circlePx = btnDecline.width
                val availablePx = totalPx - (circlePx * 2)
                adapter.itemWidth = availablePx / 3
                adapter.numbers = (1..troopCount).toList()
            }
        })

        findViewById<TextView>(R.id.Troop_Count).text = troopCount.toString()
    }

    private fun setupButtons() {
        btnShowCards = findViewById(R.id.Card_Count)
        btnShowCards.text = cardValues.size.toString()
        scrollView = findViewById(R.id.scrollView)
        container = findViewById(R.id.cardContainer)
        btnTradeIn = findViewById(R.id.btnTradeIn)
        btnTradeIn.visibility = View.GONE

        btnDecline = findViewById(R.id.btnDecline)
        btnDecline.setOnClickListener {
            troopSelector.visibility = View.GONE
        }

        btnAccept = findViewById(R.id.btnAccept)
        btnAccept.setOnClickListener {
            val lm = troopNumbers.layoutManager as LinearLayoutManager
            val snappedView = snapHelper.findSnapView(lm)
            val pos = lm.getPosition(snappedView ?: return@setOnClickListener)
            val chosen = adapter.numbers[pos]

            deployedTroops = chosen
            troopCount -= chosen

            findViewById<TextView>(R.id.Troop_Count).text = troopCount.toString()

            troopSelector.visibility = View.GONE
        }

        btnShowCards.setOnClickListener {
            if (scrollView.isGone) {
                rebuildCardContainer()
                scrollView.visibility = View.VISIBLE
                btnTradeIn.visibility = View.VISIBLE
            } else {
                scrollView.visibility = View.GONE
                btnTradeIn.visibility = View.GONE
            }
        }

        btnTradeIn.setOnClickListener {
            if (selectedIndices.isEmpty()) {
                return@setOnClickListener
            }
            selectedIndices.sortedDescending().forEach { idx ->
                cardValues.removeAt(idx)
            }
            scrollView.visibility = View.GONE
            btnTradeIn.visibility = View.GONE
            rebuildCardContainer()
        }
    }

    private fun rebuildCardContainer() {
        container.removeAllViews()
        selectedCards.clear()
        selectedIndices.clear()

        cardValues.forEachIndexed { index, value ->
            val layoutRes = when (value) {
                1 -> R.layout.card_i
                2 -> R.layout.card_ii
                3 -> R.layout.card_iii
                else -> throw IllegalArgumentException("Unexpected card value $value")
            }
            val card = layoutInflater.inflate(layoutRes, container, false)
            card.tag = index
            val defaultBg: Drawable? = card.background
            card.setOnClickListener {
                val idx = it.tag as Int
                if (selectedIndices.contains(idx)) {
                    selectedIndices.remove(idx)
                    selectedCards.remove(card)
                    card.background = defaultBg
                } else if (selectedIndices.size < 3) {
                    selectedIndices.add(idx)
                    selectedCards.add(card)
                    card.background =
                        ContextCompat.getDrawable(this, R.drawable.selected_card_border)
                }
            }
            container.addView(card)
        }
        btnShowCards.text = cardValues.size.toString()
    }
}
