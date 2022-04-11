package com.example.weatherforcastapp.network

import androidx.lifecycle.MutableLiveData
import com.example.weatherforcastapp.model.AlertsAnswer
import com.example.weatherforcastapp.model.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlertApiClient {


    var serverAlertAnswer : MutableLiveData<AlertsAnswer?>

    init {
        serverAlertAnswer = MutableLiveData()
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
                serverAlertAnswer.postValue(response.body())
            }

            override fun onFailure(call: Call<AlertsAnswer>, t: Throwable) {
                serverAlertAnswer.postValue(null)
            }
        })


    }




}