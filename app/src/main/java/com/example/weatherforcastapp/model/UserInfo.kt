package com.example.weatherforcastapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "user")
data class UserInfo(@field:ColumnInfo(name = "username")
                    @field:PrimaryKey
                    var username : String = "NA",
                    @field:ColumnInfo(name = "temperatureUnit")
                    var temperatureUnit: String = "NA",
                    @field:ColumnInfo(name = "windSpeedUnit")
                    var windSpeedUnit : String = "NA",
                    @field:ColumnInfo(name = "language")
                    var  language : String = "NA" ,
                    @field:ColumnInfo(name = "city")
                    var  city : String = "NA",
                    @field:ColumnInfo(name = "lat")
                    var  lat : String = "NA",
                    @field:ColumnInfo(name = "long")
                    var  longtude : String = "NA") : Serializable {

}