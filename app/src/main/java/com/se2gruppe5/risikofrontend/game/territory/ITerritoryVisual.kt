package com.se2gruppe5.risikofrontend.game.territory

import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord

/**
 * Wrapper for Territory providing functionality pertaining to it's visualization
 */
interface ITerritoryVisual {
    /**
     * Aggregated minimal Territory data object
     */
    val territoryRecord: TerritoryRecord

    /**
     * Visually accentuate the territory, as being currently selected
     * @param b Is this territory currently selected?
     */
    fun setHighlightSelected(b: Boolean)

    /**
     * Changes Territory Color
     * @param color colorInt of desired color
     */
    fun changeColor(color: Int)

    /**
     * Changes the Stats of the territory
     * (for now only "stats" meaning # of troops)
     */
    fun changeStat(stat: Int)

    /**
     * Observer-Pattern like system
     * Pass a functions that is to be executed upon the underlying Territory getting clicked on
     * Multiple may be provided
     * (This is to abstract away the onClickListener implementation detail)
     * A reference to whom called the function is passed as a param
     *
     * For APKs with different event systems,
     * additional adapters to facilitate the lambda functions may be needed)
     */
    fun clickSubscription(lambda: (ITerritoryVisual) -> Unit)

    /**
     * Returns the Territory's local center coordinates (in respect to window not screen)
     */
    fun getCoordinatesAsFloat(center: Boolean): Pair<Float, Float>

    fun getTerritoryId(): Int
}