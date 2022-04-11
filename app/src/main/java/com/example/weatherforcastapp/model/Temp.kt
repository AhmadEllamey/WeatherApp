package com.example.weatherforcastapp.model

import com.google.gson.annotations.SerializedName

data class Temp(
    @field:SerializedName("min")
    var min : String,
    @field:SerializedName("max")
    var max : String
)
