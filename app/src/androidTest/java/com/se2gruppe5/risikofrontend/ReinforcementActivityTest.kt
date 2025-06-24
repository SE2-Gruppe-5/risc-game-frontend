package com.se2gruppe5.risikofrontend.game

import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.se2gruppe5.risikofrontend.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReinforcementActivityTest {

    @Test
    fun tradeInButtonIsInitiallyGone() {
        ActivityScenario.launch(ReinforcementActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val tradeIn = activity.findViewById<Button>(R.id.btnTradeIn)
                assertEquals(View.GONE, tradeIn.visibility)
            }
        }
    }

    @Test
    fun initialCardCountBadgeDisplaysCorrectNumber() {
        ActivityScenario.launch(ReinforcementActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val cardCount = activity.findViewById<TextView>(R.id.Card_Count)
                // default cardValues = [2,3,1,2]
                assertEquals("4", cardCount.text.toString())
            }
        }
    }

    @Test
    fun showCardTogglesContainerAndTradeInVisibility() {
        ActivityScenario.launch(ReinforcementActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val showCards = activity.findViewById<TextView>(R.id.Card_Count)
                val scroll = activity.findViewById<HorizontalScrollView>(R.id.scrollView)
                val tradeIn = activity.findViewById<Button>(R.id.btnTradeIn)

                // Initially hidden
                assertEquals(View.GONE, scroll.visibility)
                assertEquals(View.GONE, tradeIn.visibility)

                // Click to show
                showCards.performClick()
                assertEquals(View.VISIBLE, scroll.visibility)
                assertEquals(View.VISIBLE, tradeIn.visibility)

                // Click to hide
                showCards.performClick()
                assertEquals(View.GONE, scroll.visibility)
                assertEquals(View.GONE, tradeIn.visibility)
            }
        }
    }

    @Test
    fun selectingAndUnselectingCardsTogglesBackground() {
        ActivityScenario.launch(ReinforcementActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val showCards = activity.findViewById<TextView>(R.id.Card_Count)
                val container = activity.findViewById<LinearLayout>(R.id.cardContainer)

                // Build and show cards
                showCards.performClick()
                val cardView = container.getChildAt(0)
                val defaultBg = cardView.background
                val selectedBorder = ContextCompat.getDrawable(activity, R.drawable.selected_card_border)

                // Select the card
                cardView.performClick()
                assertNotEquals(defaultBg, cardView.background)
                assertEquals(selectedBorder?.constantState, cardView.background.constantState)

                // Unselect the same card
                cardView.performClick()
                assertEquals(defaultBg.constantState, cardView.background.constantState)
            }
        }
    }

    @Test
    fun selectingUpToThreeCardsAndLimitSelection() {
        ActivityScenario.launch(ReinforcementActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val showCards = activity.findViewById<TextView>(R.id.Card_Count)
                val container = activity.findViewById<LinearLayout>(R.id.cardContainer)
                showCards.performClick() // build and show cards

                // select first three cards
                for (i in 0..2) {
                    container.getChildAt(i).performClick()
                }
                // fourth click should do nothing (no exception)
                container.getChildAt(3).performClick()
                // still only 3 selected: by checking backgrounds
                val selected = (0 until container.childCount).count { idx ->
                    container.getChildAt(idx).background.constantState ==
                            ContextCompat.getDrawable(activity, R.drawable.selected_card_border)?.constantState
                }
                assertEquals(3, selected)
            }
        }
    }

    @Test
    fun tradeInWithoutSelectionKeepsUIVisibleAndCountUnchanged() {
        ActivityScenario.launch(ReinforcementActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val showCards = activity.findViewById<TextView>(R.id.Card_Count)
                val scroll = activity.findViewById<HorizontalScrollView>(R.id.scrollView)
                val tradeIn = activity.findViewById<Button>(R.id.btnTradeIn)

                // Show cards (tradeIn now visible)
                showCards.performClick()
                assertEquals(View.VISIBLE, scroll.visibility)
                assertEquals(View.VISIBLE, tradeIn.visibility)

                val beforeCount = showCards.text.toString().toInt()
                // Click tradeIn with no cards selected
                tradeIn.performClick()

                // UI remains visible and count unchanged
                assertEquals(View.VISIBLE, scroll.visibility)
                assertEquals(View.VISIBLE, tradeIn.visibility)
                assertEquals(beforeCount, showCards.text.toString().toInt())
            }
        }
    }

    @Test
    fun tradeInRemovesSelectedCardsAndCollapsesUI() {
        ActivityScenario.launch(ReinforcementActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val showCards = activity.findViewById<TextView>(R.id.Card_Count)
                val container = activity.findViewById<LinearLayout>(R.id.cardContainer)
                val scroll = activity.findViewById<HorizontalScrollView>(R.id.scrollView)
                val tradeIn = activity.findViewById<Button>(R.id.btnTradeIn)

                showCards.performClick()

                // select two cards (positions 0 and 1)
                container.getChildAt(0).performClick()
                container.getChildAt(1).performClick()
                assertEquals(View.VISIBLE, tradeIn.visibility)

                // record initial count
                val initialSize = showCards.text.toString().toInt()

                // trade in
                tradeIn.performClick()

                // UI collapses
                assertEquals(View.GONE, scroll.visibility)
                assertEquals(View.GONE, tradeIn.visibility)

                // New card count should be initial minus 2
                val newSize = showCards.text.toString().toInt()
                assertEquals(initialSize - 2, newSize)
            }
        }
    }

    @Test
    fun territoryButtonShowsTroopSelector() {
        ActivityScenario.launch(ReinforcementActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val territoryBtn = activity.findViewById<ImageButton>(R.id.territoryAbtn)
                val troopSelector = activity.findViewById<View>(R.id.troopSelector)
                // Initially hidden
                assertEquals(View.GONE, troopSelector.visibility)
                territoryBtn.performClick()
                assertEquals(View.VISIBLE, troopSelector.visibility)
            }
        }
    }

    @Test
    fun declineButtonHidesTroopSelectorWithoutChangingTroopCount() {
        ActivityScenario.launch(ReinforcementActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val territoryBtn = activity.findViewById<ImageButton>(R.id.territoryAbtn)
                val declineBtn = activity.findViewById<ImageView>(R.id.btnDecline)
                val troopSelector = activity.findViewById<View>(R.id.troopSelector)
                val badge = activity.findViewById<TextView>(R.id.Troop_Count)

                val beforeCount = badge.text.toString()

                territoryBtn.performClick()
                assertEquals(View.VISIBLE, troopSelector.visibility)
                declineBtn.performClick()

                assertEquals(View.GONE, troopSelector.visibility)
                // troopCount unchanged
                assertEquals(beforeCount, badge.text.toString())
            }
        }
    }

    @Test
    fun troopCountBadgeDisplaysCorrectNumber() {
        ActivityScenario.launch(ReinforcementActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val badge = activity.findViewById<TextView>(R.id.Troop_Count)
                assertEquals("10", badge.text.toString())
            }
        }
    }
}
