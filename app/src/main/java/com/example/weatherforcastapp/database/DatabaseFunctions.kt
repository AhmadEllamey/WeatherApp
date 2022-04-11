package com.example.weatherforcastapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforcastapp.model.Cities
import com.example.weatherforcastapp.model.UserInfo


@Dao
interface DatabaseFunctions {



    @Query("SELECT * From user WHERE username = :username")
    fun getCurrentUser(username : String) : UserInfo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserInfo(user : UserInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCity(city : Cities)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCities(cities : List<Cities>)

    @Query("SELECT * From cities WHERE username = :username")
    fun getCities(username: String) : List<Cities>

    @Query("DELETE  From cities WHERE username = :username")
    fun clearCities(username: String)

    @Query("DELETE  From cities WHERE username = :username AND city = :city")
    fun removeCity(username: String , city : String)

}