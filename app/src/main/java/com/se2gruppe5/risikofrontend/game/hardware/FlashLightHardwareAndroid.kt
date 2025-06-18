package com.se2gruppe5.risikofrontend.game.hardware

import android.content.Context
import android.hardware.camera2.CameraManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FlashLightHardwareAndroid private constructor (private val context: Context) : IFlashLightHardware {

    //Credit: https://stackoverflow.com/questions/40398072/singleton-with-parameter-in-kotlin
    companion object {
        @Volatile
        private var INSTANCE: IFlashLightHardware? = null

        fun getInstance(context: Context): IFlashLightHardware {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FlashLightHardwareAndroid(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private var camID: String? = null
    private var isLightOn: Boolean = false

    //Settings
    private val blinkTimes: Int = 5
    private val blinkDelay: Long = 200
    private var blinkTasker: Job? = null

    private val cameraManager: CameraManager by lazy {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    init {
        findCam()
    }

    /**
     * Attempts to find an appropriate phone camera,
     * saves ID to camID if so.
     */
    private fun findCam() {
        for (id in cameraManager.cameraIdList) {
            if (camHasFlash(id) == true && isCamBackFacing(id) == true) {
                camID = id
                break
            }
        }
    }

    /**
     * Ascertain whether Phone Camera has flash-light
     */
    private fun camHasFlash(id: String): Boolean? {
        return cameraManager.getCameraCharacteristics(id)
            .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE)
    }

    /**
     * Ascertain whether Phone Camera is back-facing (i.e. not a selfie camera)
     */
    private fun isCamBackFacing(id: String): Boolean? {
        return cameraManager.getCameraCharacteristics(id)
            .get(android.hardware.camera2.CameraCharacteristics.LENS_FACING) ==
                android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK
    }

    override fun turnOn() {
        camID?.let {
            cameraManager.setTorchMode(it, true)
            isLightOn = true
        }
    }

    override fun turnOff() {
        camID?.let {
            cameraManager.setTorchMode(it, false)
            isLightOn = false
        }
    }

    override fun toggle() {
        camID?.let {
            cameraManager.setTorchMode(it, !isLightOn)
            isLightOn = !isLightOn
        }
    }

    //To be completely honest, I do not fully understand coroutines.
    //I do however concede that one mustn't perform such a task without them (or similar solutions)
    @OptIn(DelicateCoroutinesApi::class)
    override fun blink() {
        blinkTasker?.cancel()
        blinkTasker = CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {
            repeat(blinkTimes) {
                turnOn()
                delay(blinkDelay)
                turnOff()
                delay(blinkDelay)
            }
        }
    }
}
