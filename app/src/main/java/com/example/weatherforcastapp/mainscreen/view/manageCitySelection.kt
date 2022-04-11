package com.example.weatherforcastapp.mainscreen.view

import com.example.weatherforcastapp.model.Cities

interface ManageCitySelection {

    fun onClickFunction(city : Cities)

    fun onLongClickFunction(city : Cities)

}