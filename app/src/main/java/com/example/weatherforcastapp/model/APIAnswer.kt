package com.example.weatherforcastapp.model

import com.google.gson.annotations.SerializedName

data class APIAnswer(
    @field:SerializedName("current")
    var current : Currently,
    @field:SerializedName("hourly")
    var hourly : List<Hourly>,
    @field:SerializedName("daily")
    var daily : List<Daily>
)
