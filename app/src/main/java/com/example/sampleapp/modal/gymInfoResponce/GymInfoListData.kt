package com.example.sampleapp.modal.gymInfoResponce

import com.example.sampleapp.modal.gymListResponce.GymListData
import com.google.gson.annotations.SerializedName

data class GymInfoListData(
    @SerializedName("imageBaseURL") var imageBaseURL: String? = null,
    @SerializedName("gym") var gym: GymListData? = GymListData(),
//    @SerializedName("gymImages") var gymImages: String? = null
)