package com.example.weatherforcastapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClassFactory {


    companion object{

        private var retrofit: Retrofit? = null

        fun getClient(baseUrl: String?): Retrofit? {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl!!)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }

    }


}