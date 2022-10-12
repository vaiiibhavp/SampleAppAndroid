package com.example.sampleapp.viewModal

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.sampleapp.modal.gymInfoResponce.GymInfoListData
import com.example.sampleapp.widget.dialog.ProgressDialog


class GymInfoViewModel : BaseViewModel() {

    internal var gymInfoDetailData = MutableLiveData<GymInfoListData>()

    fun getGymInfoDetail(id: String, cntxt: AppCompatActivity) {
        val dialog = ProgressDialog(cntxt)
        dialog.show()
        gymServiceRepository?.getGymInfoList(id = id)?.observeForever {
            it?.let {
                dialog.dismiss()
                gymInfoDetailData.value = it
            }
        }
    }

}
