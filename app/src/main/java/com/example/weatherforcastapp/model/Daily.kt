package com.example.weatherforcastapp.model

import com.google.gson.annotations.SerializedName

data class Daily(
    @field:SerializedName("dt")
    var dt : String,
    @field:SerializedName("temp")
    var temp : Temp,
    @field:SerializedName("weather")
    var weather : List<Weather>
)
