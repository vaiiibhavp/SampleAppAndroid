package com.example.sampleapp.Utils

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


object UtilityClass {


    private val TAG: String = UtilityClass::class.java.getSimpleName()

    fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    fun getTodayDate(format: String?): String {
        val dateFormat: DateFormat =
            SimpleDateFormat(format, Locale.ENGLISH)
        val date = Date()
        return dateFormat.format(date)
    }

    fun getLatLongDistance(context: Context,lat: Double,long : Double) : Double{
        val selected_location = Location("locationA")
        selected_location.setLatitude(lat)
        selected_location.setLongitude(long)
        val near_locations = Location("locationB")
        context.let {
           var lat = MyShare(it).latitude?.let { return@let it }
            near_locations.setLatitude(lat!!.toDouble())
        }
        context.let {
            var logn = MyShare(it).logitude?.let { return@let it }
            near_locations.setLongitude(logn!!.toDouble())
        }
        val distance: Double = (selected_location.distanceTo(near_locations).toDouble())/1000
        var finaldistance = DecimalFormat("##.##").format(distance)
        return finaldistance.toDouble()
    }

     fun appInstalledOrNot(context: Context,uri: String): Boolean {
        val pm: PackageManager = context.getPackageManager()
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            println("fdsafjd::>>")
        }
        return false
    }
}