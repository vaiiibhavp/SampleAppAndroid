package com.example.sampleapp.listener

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.sampleapp.Utils.Constants.DATE_FORMAT
import com.example.sampleapp.Utils.MyShare
import com.example.sampleapp.Utils.UtilityClass


class StepListener : Service(), SensorEventListener {
    var sensorManager: SensorManager? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val stepCounter = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        if (stepCounter != null) {
            val batchMode = sensorManager!!.registerListener(
                this, stepCounter, SensorManager.SENSOR_DELAY_FASTEST, 2000000
            )
            if (!batchMode) {
                Log.e(
                    "BATCH_MODE",
                    "~~~~~~~~~~~Could not register batch mode for sensor~~~~~~~~~~~"
                )
                Toast.makeText(
                    applicationContext,
                    "Could not register batch mode for sensor",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
              //  Toast.makeText(this, "Started Counting Steps", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Device not Compatible!", Toast.LENGTH_LONG).show()
            this.stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
            if (steps == 0){
                this.let {
                    if (MyShare(it).currentDate?.let { return@let it }.equals(UtilityClass.getTodayDate(DATE_FORMAT))){
                        steps = MyShare(it).stepCount!!
                    }else{
                        steps = 0
                    }
                }
            }
            steps++
           // MainActivity.STEPS = steps
            this.let {
                MyShare(it).stepCount = steps
            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    companion object {
        var steps = 0
    }
}