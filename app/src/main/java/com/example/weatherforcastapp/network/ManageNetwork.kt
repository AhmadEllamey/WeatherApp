package com.example.weatherforcastapp.network

import com.example.weatherforcastapp.model.APIAnswer
import com.example.weatherforcastapp.model.AlertsAnswer

interface ManageNetwork {

    fun onRequestSuccess(temp : APIAnswer)
    fun onRequestFailed(msg: String)

    fun onRequestSuccessForSecondLanguage(temp : APIAnswer,language: String)
    fun onRequestFailedForSecondLanguage(msg: String)


    fun onAlertRequestSuccess(alerts : AlertsAnswer?)
    fun onAlertRequestFailed(msg: String)

    fun onAlertRequestSuccessForTheSecondLanguage(alerts : AlertsAnswer?,language: String)
    fun onAlertRequestFailedForTheSecondLanguage(msg: String)

}