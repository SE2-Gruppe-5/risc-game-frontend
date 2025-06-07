package com.se2gruppe5.risikofrontend.game.hardware

interface IShakeHardware {
    fun sensorRegisterListener ()
    fun sensorDeRegisterListener()
    fun setInteractionLambdaSubscription(lambda: () -> Unit)
}
