package com.example.sampleapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.sampleapp.Utils.MyShare
import com.example.sampleapp.viewModal.ExerciseSessionViewModel
import java.util.*

class StepCallService : Service() {
    private var timer: Timer? = null
    private var task: TimerTask? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private val exerciseSessionViewModel: ExerciseSessionViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)
            .create(ExerciseSessionViewModel::class.java)
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")

        val delay = GregorianCalendar().getTimeInMillis()+24*3600000 // delay for 1 day.
        val period = GregorianCalendar().getTimeInMillis()+24*3600000 // repeat 1 day.
//        val delay = 5000 // delay for 5 sec.
//        val period = 5000 // repeat every sec.
        timer = Timer()
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                exerciseSessionViewModel.let {
                    if (MyShare(this@StepCallService).stepCount!! != 0) {
                        exerciseSessionViewModel.insertExerciseSession(MyShare(this@StepCallService).stepCount!!)
                        exerciseSessionViewModel.initialLoad()
                    }

                }

            }
        }.also { task = it }, delay.toLong(), period.toLong())
    }

    override fun stopService(name: Intent): Boolean {
        // TODO Auto-generated method stub
        timer!!.cancel()
        task!!.cancel()
        return super.stopService(name)
    }

    companion object {
        private const val TAG = "AutoService"
    }
}