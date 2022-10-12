package com.example.sampleapp.Utils

import android.content.Context
import android.content.SharedPreferences
import com.example.sampleapp.Utils.Constants.CURRENT_DATE
import com.example.sampleapp.Utils.Constants.GYMLISTBASEIMAGEURL
import com.example.sampleapp.Utils.Constants.LATITUDE
import com.example.sampleapp.Utils.Constants.LONGITUDE
import com.example.sampleapp.Utils.Constants.PREFE_NAME
import com.example.sampleapp.Utils.Constants.STEPS_COUNT

class MyShare (context: Context) {
    private val context: Context
    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        this.context = context
        sharedPreferences = context.getSharedPreferences(PREFE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    var currentDate: String?
        get() = sharedPreferences.getString(CURRENT_DATE, "00/00/0000")
        set(currentDate) {
            currentDate.let{
                editor.putString(CURRENT_DATE, it)
                editor.apply()
            }
        }

    var stepCount: Int?
        get() = sharedPreferences.getInt(STEPS_COUNT, 0)
        set(stepCount) {
            stepCount.let{
                editor.putInt(STEPS_COUNT, it!!)
                editor.apply()
            }
        }
    var latitude: String?
        get() = sharedPreferences.getString(LATITUDE, "0")
        set(stepCount) {
            stepCount.let{
                editor.putString(LATITUDE, it!!)
                editor.apply()
            }
        }

    var logitude: String?
        get() = sharedPreferences.getString(LONGITUDE, "0")
        set(stepCount) {
            stepCount.let{
                editor.putString(LONGITUDE, it!!)
                editor.apply()
            }
        }

    var gymListBaseImageUrl: String?
        get() = sharedPreferences.getString(GYMLISTBASEIMAGEURL, "0")
        set(stepCount) {
            stepCount.let{
                editor.putString(GYMLISTBASEIMAGEURL, it!!)
                editor.apply()
            }
        }


}