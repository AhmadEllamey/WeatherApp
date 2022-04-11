package com.example.weatherforcastapp.model

import com.google.gson.annotations.SerializedName

data class Hourly(
    @field:SerializedName("dt")
    var dt : String,
    @field:SerializedName("temp")
    var temp : String,
    @field:SerializedName("weather")
    var weather : List<Weather>
)
