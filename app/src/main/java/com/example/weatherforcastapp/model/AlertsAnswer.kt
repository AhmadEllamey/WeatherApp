package com.example.weatherforcastapp.model

import com.google.gson.annotations.SerializedName

data class AlertsAnswer(
    @field:SerializedName("alerts")
    var listOfAlerts : List<Alerts>
)
