package com.example.sampleapp.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GymRepository {
    private var gymServiceAPI: GymServiceAPI? = null

    companion object {
        private var gymRepository: GymRepository? = null

        @Synchronized
        fun getInstance(): GymRepository? {
            if (gymRepository == null) {
                gymRepository = GymRepository()
            }
            return gymRepository
        }
    }

    init {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://65.20.69.210:4000/")
            .build()

        gymServiceAPI = retrofit.create(GymServiceAPI::class.java)
    }


}