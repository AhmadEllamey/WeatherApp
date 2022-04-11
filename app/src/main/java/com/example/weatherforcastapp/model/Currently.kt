package com.example.weatherforcastapp.model

import com.google.gson.annotations.SerializedName

data class Currently(
    @field:SerializedName("dt")
    var dt : String,
    @field:SerializedName("temp")
    var temp : String,
    @field:SerializedName("pressure")
    var pressure : String,
    @field:SerializedName("humidity")
    var humidity : String,
    @field:SerializedName("uvi")
    var uvi : String,
    @field:SerializedName("clouds")
    var clouds : String,
    @field:SerializedName("visibility")
    var visibility : String,
    @field:SerializedName("wind_speed")
    var wind_speed : String,
    @field:SerializedName("weather")
    var weather : List<Weather>
)
