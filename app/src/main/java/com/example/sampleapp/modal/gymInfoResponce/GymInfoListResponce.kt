package com.example.sampleapp.modal.gymInfoResponce

import com.example.sampleapp.modal.BaseResponse
import com.google.gson.annotations.SerializedName

data class GymInfoListResponce(
    @SerializedName("data") var data: GymInfoListData? = GymInfoListData()
) : BaseResponse()