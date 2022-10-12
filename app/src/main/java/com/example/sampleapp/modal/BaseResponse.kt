package com.example.sampleapp.modal

import com.google.gson.annotations.SerializedName

abstract class BaseResponse(
    @SerializedName("code") var code: Int? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null
)
