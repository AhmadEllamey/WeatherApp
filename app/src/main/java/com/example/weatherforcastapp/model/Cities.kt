package com.example.weatherforcastapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cities")
data class Cities(
    @field:ColumnInfo(name = "username")
    var username : String = "" ,
    @field:ColumnInfo(name = "lat")
    var lat : Double = 0.0 ,
    @field:ColumnInfo(name = "lng")
    var lng : Double = 0.0 ,
    @field:ColumnInfo(name = "cityAr")
    var cityAr : String = "" ,
    @field:ColumnInfo(name = "city")
    @field:PrimaryKey
    var city : String = "")
