package com.example.weatherforcastapp.network


import com.example.weatherforcastapp.mainscreen.view.MainScreen
import com.example.weatherforcastapp.model.APIAnswer
import com.example.weatherforcastapp.model.AlertsAnswer
import com.example.weatherforcastapp.model.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLDecoder

class APIClient( manageNetwork: ManageNetwork) {

    var manageNetwork: ManageNetwork

    init {
        this.manageNetwork = manageNetwork
    }



    fun getTheTemp(lat : String , lng : String ,language : String ,unit :String) {
        println("hello from the getTheTemp method")
        val BaseUrl = "https://api.openweathermap.org/data/2.5/"
        val retrofitHelper: RetrofitHelper = RetrofitClassFactory.getClient(BaseUrl)!!.create(
            RetrofitHelper::class.java
        )
        val linkToGo = "onecall?lat=$lat&lon=$lng&exclude=minutely,alerts&lang=$language&units=$unit&appid=9c33271eb9374e70e2266d93e0d49785"
        println("$BaseUrl$linkToGo")
        val call = retrofitHelper.getTemp(linkToGo)
        call.enqueue(object : Callback<APIAnswer> {
            override fun onResponse(call: Call<APIAnswer>, response: Response<APIAnswer>) {
                println("hello from the success method")
                println(linkToGo)
                println(response.body())
                println(call.request().url())
                println(manageNetwork)
                manageNetwork.onRequestSuccess(response.body()!!)
            }

            override fun onFailure(call: Call<APIAnswer>, t: Throwable) {
                println("hello from the failed method")
                manageNetwork.onRequestFailed("failed -----> $t")
            }
        })
    }

    fun getTheSecondLanguageInfo(lat : String , lng : String ,language : String ,unit :String){

        println("hello from the getTheTemp method")
        val BaseUrl = "https://api.openweathermap.org/data/2.5/"
        val retrofitHelper: RetrofitHelper = RetrofitClassFactory.getClient(BaseUrl)!!.create(
            RetrofitHelper::class.java
        )
        val linkToGo = "onecall?lat=$lat&lon=$lng&exclude=minutely,alerts&lang=$language&units=$unit&appid=9c33271eb9374e70e2266d93e0d49785"
        println("$BaseUrl$linkToGo")
        val call = retrofitHelper.getTemp(linkToGo)
        call.enqueue(object : Callback<APIAnswer> {
            override fun onResponse(call: Call<APIAnswer>, response: Response<APIAnswer>) {
                println("hello from the success method")
                println(linkToGo)
                println(response.body())
                println(call.request().url())
                println(manageNetwork)

                val currentUnitToSave = when(unit){
                    "metric"   ->{
                        "Celsius"
                    }

                    "imperial"   ->{
                        "Fahrenheit"
                    }

                    else -> {
                        "Kelvin"
                    }
                }
                MainScreen.setTheCurrentUnit(currentUnitToSave)

                manageNetwork.onRequestSuccessForSecondLanguage(response.body()!!,language)
            }

            override fun onFailure(call: Call<APIAnswer>, t: Throwable) {
                println("hello from the failed method")
                manageNetwork.onRequestFailedForSecondLanguage("failed -----> $t")
            }
        })


    }

    fun getTheAlerts(userInfo: UserInfo){

        val BaseUrl = "https://api.openweathermap.org/data/2.5/"
        val retrofitHelper: RetrofitHelper = RetrofitClassFactory.getClient(BaseUrl)!!.create(
            RetrofitHelper::class.java
        )
        val linkToGo = "onecall?lat=${userInfo.lat}&lon=${userInfo.longtude}&exclude=minutely,current,hourly,daily&lang=${userInfo.language}&units=${userInfo.temperatureUnit}&appid=9c33271eb9374e70e2266d93e0d49785"
        val call = retrofitHelper.getAlert(linkToGo)

        call.enqueue(object : Callback<AlertsAnswer> {
            override fun onResponse(call: Call<AlertsAnswer>, response: Response<AlertsAnswer>) {
                manageNetwork.onAlertRequestSuccess(response.body())
            }

            override fun onFailure(call: Call<AlertsAnswer>, t: Throwable) {
                manageNetwork.onAlertRequestFailed("failed -----> $t")
            }
        })


    }

    fun getTheAlertsWithTheSecondLanguage(userInfo: UserInfo,language: String){

        val BaseUrl = "https://api.openweathermap.org/data/2.5/"
        val retrofitHelper: RetrofitHelper = RetrofitClassFactory.getClient(BaseUrl)!!.create(
            RetrofitHelper::class.java
        )
        val linkToGo = "onecall?lat=${userInfo.lat}&lon=${userInfo.longtude}&exclude=minutely,current,hourly,daily&lang=$language&units=${userInfo.temperatureUnit}&appid=9c33271eb9374e70e2266d93e0d49785"
        val call = retrofitHelper.getAlert(linkToGo)

        call.enqueue(object : Callback<AlertsAnswer> {
            override fun onResponse(call: Call<AlertsAnswer>, response: Response<AlertsAnswer>) {
                manageNetwork.onAlertRequestSuccessForTheSecondLanguage(response.body(),language)
            }

            override fun onFailure(call: Call<AlertsAnswer>, t: Throwable) {
                manageNetwork.onAlertRequestFailedForTheSecondLanguage("failed -----> $t")
            }
        })


    }



}