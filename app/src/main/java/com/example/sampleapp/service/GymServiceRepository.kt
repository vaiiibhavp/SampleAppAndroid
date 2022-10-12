package com.example.sampleapp.service

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sampleapp.R
import com.example.sampleapp.Utils.MyShare
import com.example.sampleapp.app.ServiceApp
import com.example.sampleapp.modal.gymInfoResponce.GymInfoListData
import com.example.sampleapp.modal.gymInfoResponce.GymInfoListResponce
import com.example.sampleapp.modal.gymListResponce.GymListResponce
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class GymServiceRepository {
    private var gymServiceAPI: GymServiceAPI? = null
    private val context = ServiceApp.context

    companion object {
        private var gymServiceRepository: GymServiceRepository? = null

        @Synchronized
        fun getInstance(): GymServiceRepository? {
            if (gymServiceRepository == null) {
                gymServiceRepository = GymServiceRepository()
            }
            return gymServiceRepository
        }
    }

    init {

        val okHttp = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .cache(null)

//        val token = getToken()
//
//        if (context != null && token.isNotEmpty()) {
//            okHttp.addInterceptor(Interceptor {
//                val newRequest = it.request().newBuilder()
//                    .addHeader("Accept", "application/json")
//                    .addHeader("Authorization", "Bearer $token")
//                    .build()
//                return@Interceptor it.proceed(newRequest)
//            })
//        }

        val okHttpClient = okHttp.build()
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .baseUrl(GymServiceAPI.API_BASE_URL)
            .build()

        gymServiceAPI = retrofit.create(GymServiceAPI::class.java)

    }

    fun getGymList(limit: String, page: String): LiveData<GymListResponce?> {
        val data: MutableLiveData<GymListResponce?> = MutableLiveData()

        gymServiceAPI?.getGymList(limit = limit, page = page)
            ?.enqueue(object : Callback<GymListResponce?> {
                override fun onResponse(
                    call: Call<GymListResponce?>,
                    response: Response<GymListResponce?>
                ) {
                    response.body()?.let {
                        data.value = it
                        MyShare(context!!).gymListBaseImageUrl = it.imageBaseURL
                    } ?: run {
                        Toast.makeText(
                            context,
                            context?.resources?.getString(R.string.somthing_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GymListResponce?>, t: Throwable) {
                    t.printStackTrace()
                    data.value = null
                }
            })
        return data
    }

    fun getGymInfoList(id:String): LiveData<GymInfoListData?> {
        val data: MutableLiveData<GymInfoListData?> = MutableLiveData()

        gymServiceAPI?.getGymInfoList(id)
            ?.enqueue(object : Callback<GymInfoListResponce?> {
                override fun onResponse(
                    call: Call<GymInfoListResponce?>,
                    response: Response<GymInfoListResponce?>
                ) {
                    response.body()?.let {
                        data.value = it.data
                    } ?: run {
                        Toast.makeText(
                            context,
                            context?.resources?.getString(R.string.somthing_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GymInfoListResponce?>, t: Throwable) {
                    t.printStackTrace()
                    data.value = null
                }
            })
        return data
    }


}