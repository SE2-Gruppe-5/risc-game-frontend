package com.se2gruppe5.risikofrontend.game.dice.diceHardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class DiceHardwareAndroid(private val context: Context) : IDiceHardware, SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var shakeDelta = 0f
    private var shakeMeasurementCurrent = 0f
    private var shakeMeasurementLast = 0f
    private var shakeLambda: () -> Unit = {}

    //Settings
    private val shakeDetectionThreshold = 15
    private val shakeAverageSoften = 0.8f // 0.0f < x < 1.0f

    override fun sensorRegisterListener() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
        shakeMeasurementCurrent = SensorManager.GRAVITY_EARTH
        shakeMeasurementLast = SensorManager.GRAVITY_EARTH
    }

    override fun sensorDeRegisterListener() {
        sensorManager?.unregisterListener(this)
    }

    override fun setInteractionLambdaSubscription(lambda: () -> Unit) {
        shakeLambda = lambda
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        //Detect next measurement
        shakeMeasurementLast = shakeMeasurementCurrent
        //To compute linear accel. in 3d: |a| = sqrt(x^2+y^2+z^2)
        shakeMeasurementCurrent = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        //Soften out shakes by retaining recent accelerations
        shakeDelta = shakeDelta * shakeAverageSoften
        //Add newest measurement to sum
        shakeDelta += shakeMeasurementCurrent - shakeMeasurementLast

        detectShake()
    }
    private fun detectShake(){
        if (shakeDelta > shakeDetectionThreshold) {
            //Stop listening for shakes and call lambda (this is probably always 'roll()')
            sensorDeRegisterListener()
            shakeLambda()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

