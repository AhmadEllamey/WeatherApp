package com.example.weatherforcastapp.model

import com.google.gson.annotations.SerializedName

data class Alerts(
    @field:SerializedName("description")
    var description : String)
