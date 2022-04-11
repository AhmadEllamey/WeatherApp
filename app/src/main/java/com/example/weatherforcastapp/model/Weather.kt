package com.example.weatherforcastapp.model

import com.google.gson.annotations.SerializedName

data class Weather(
    @field:SerializedName("main")
    var mainText : String,
    @field:SerializedName("description")
    var description : String
)