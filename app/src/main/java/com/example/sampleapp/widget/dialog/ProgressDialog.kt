package com.example.sampleapp.widget.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.activity.ComponentActivity
import com.example.sampleapp.databinding.DialogProgressbarBinding

data class ProgressDialog(val cntxt: ComponentActivity) : Dialog(cntxt) {
    private var dialogbinding: DialogProgressbarBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogbinding = DialogProgressbarBinding.inflate(LayoutInflater.from(cntxt))

        dialogbinding?.let{
            setContentView(it.root)
        }
        setCanceledOnTouchOutside(false)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    }
}