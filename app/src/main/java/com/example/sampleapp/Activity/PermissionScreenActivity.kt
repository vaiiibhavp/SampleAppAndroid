package com.example.sampleapp.Activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import com.example.sampleapp.MainActivity
import com.example.sampleapp.R
import com.example.sampleapp.Utils.UtilityClass

class PermissionScreenActivity : AppCompatActivity() {

    companion object {
        private val SENSOR_CODE = 567
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_screen)


    }

    override fun onResume() {
        super.onResume()
        if (HealthConnectClient.isAvailable(this)) {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )

//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
//                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
//                   SENSOR_CODE
//                )
//            }

        } else {
            var appInstall = UtilityClass.appInstalledOrNot(
                this@PermissionScreenActivity,
                this@PermissionScreenActivity.resources.getString(R.string.pakegeName)
            )
            if (!appInstall) {
                val dialog = Dialog(this@PermissionScreenActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.dialog_app_install)

                val dialogButton: Button = dialog.findViewById(R.id.btn_yes) as Button
                dialogButton.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        var appIntent = Intent(Intent.ACTION_VIEW);
                        appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        appIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata"));
                        startActivity(appIntent);
                    }
                })
                dialog.show()
            }

        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted) {
                    var Intenttt = Intent(this, MainActivity::class.java)
                    startActivity(Intenttt)
                    // Permission is granted
                } else {
                    // Permission is denied
                }
            }
        }


}