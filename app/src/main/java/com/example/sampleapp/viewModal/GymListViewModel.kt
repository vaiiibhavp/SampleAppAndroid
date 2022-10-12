package com.example.sampleapp.viewModal

import androidx.activity.ComponentActivity
import androidx.lifecycle.MutableLiveData
import com.example.sampleapp.modal.gymListResponce.GymListResponce
import com.example.sampleapp.widget.dialog.ProgressDialog


class GymListViewModel : BaseViewModel() {

    internal var gymDetailData = MutableLiveData<GymListResponce>()

    fun getGymDetail(limit: String, page : String, cntxt: ComponentActivity) {
        val dialog = ProgressDialog(cntxt)
        dialog.show()
        gymServiceRepository?.getGymList(limit = limit,page = page)?.observeForever {
            it?.let {
                dialog.dismiss()
                gymDetailData.value = it
            }
        }
    }

}
