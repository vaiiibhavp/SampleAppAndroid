package com.example.sampleapp.service

import android.util.ArrayMap
import com.example.sampleapp.modal.createGym.CreateGymResponce
import com.example.sampleapp.modal.gymInfoResponce.GymInfoListResponce
import com.example.sampleapp.modal.gymListResponce.GymListResponce
import retrofit2.Call
import retrofit2.http.*

interface GymServiceAPI {

    companion object {
        private const val BASE_URL = "http://65.20.69.210:4000/"
        const val API_BASE_URL = "${BASE_URL}api/v1/"
    }

    @POST("gym")
    fun postCreateGym(
        @FieldMap params: ArrayMap<String?, Any?>?
    ): Call<CreateGymResponce?>?

    @GET("gym")
    fun getGymList(
        @Query("limit") limit: String,
        @Query("page") page: String,
    ): Call<GymListResponce?>?

    @GET("gym/{ID}")
    fun getGymInfoList(@Path("ID") id: String): Call<GymInfoListResponce?>?

    @GET("gym/search")
    fun getSearchGym(
        @Query("lat") latitude: String,
        @Query("lng") logitude: String,
    ): Call<GymInfoListResponce?>?
}