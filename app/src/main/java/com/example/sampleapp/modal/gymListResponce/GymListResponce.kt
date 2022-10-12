package com.example.sampleapp.modal.gymListResponce

import com.example.sampleapp.modal.BaseResponse
import com.google.gson.annotations.SerializedName

data class GymListResponce(
    @SerializedName("data") var data: ArrayList<GymListData> = arrayListOf(),
    @SerializedName("imageBaseURL") var imageBaseURL: String? = null,
    @SerializedName("page") var page: Int? = null,
    @SerializedName("limit") var limit: Int? = null,
    @SerializedName("totalRecords") var totalRecords: Int? = null
) : BaseResponse()