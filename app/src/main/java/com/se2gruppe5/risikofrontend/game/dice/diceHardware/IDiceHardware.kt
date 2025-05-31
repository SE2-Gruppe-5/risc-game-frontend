package com.se2gruppe5.risikofrontend.game.dice.diceHardware

interface IDiceHardware {
    fun sensorRegisterListener ()
    fun sensorDeRegisterListener()
    fun setInteractionLambdaSubscription(lambda: () -> Unit)
}
