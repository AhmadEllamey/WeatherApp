package com.example.weatherforcastapp.database

import com.example.weatherforcastapp.MainActivity
import com.example.weatherforcastapp.model.Cities
import com.example.weatherforcastapp.model.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Repo {

    lateinit var databaseFunctions : DatabaseFunctions


    // insert a user info
    fun insertTheUser(user : UserInfo){
        val appDataBase: AppDatabase = AppDatabase.getInstance(MainActivity.getTheContext())!!
        databaseFunctions = appDataBase.databaseFunctions()!!
        databaseFunctions.insertUserInfo(user)
    }

    // get the user
    fun getTheUser(username : String) : UserInfo{
        val appDataBase: AppDatabase = AppDatabase.getInstance(MainActivity.getTheContext())!!
        databaseFunctions = appDataBase.databaseFunctions()!!
        return databaseFunctions.getCurrentUser(username)
    }

    fun insertCity(city:Cities){
        val appDataBase: AppDatabase = AppDatabase.getInstance(MainActivity.getTheContext())!!
        databaseFunctions = appDataBase.databaseFunctions()!!
        databaseFunctions.insertCity(city)
    }

    fun insertCities(cities: List<Cities>){
        val appDataBase: AppDatabase = AppDatabase.getInstance(MainActivity.getTheContext())!!
        databaseFunctions = appDataBase.databaseFunctions()!!
        CoroutineScope(Dispatchers.IO).launch {
            databaseFunctions.insertCities(cities)
        }

    }

    fun getCities(username: String) : List<Cities> {
        val appDataBase: AppDatabase = AppDatabase.getInstance(MainActivity.getTheContext())!!
        databaseFunctions = appDataBase.databaseFunctions()!!
        return databaseFunctions.getCities(username)
    }

    fun clearCities(username: String){
        val appDataBase: AppDatabase = AppDatabase.getInstance(MainActivity.getTheContext())!!
        databaseFunctions = appDataBase.databaseFunctions()!!
        CoroutineScope(Dispatchers.IO).launch {
            databaseFunctions.clearCities(username)
        }

    }

    fun removeCity(username: String,city:String){
        val appDataBase: AppDatabase = AppDatabase.getInstance(MainActivity.getTheContext())!!
        databaseFunctions = appDataBase.databaseFunctions()!!
        CoroutineScope(Dispatchers.IO).launch {
            databaseFunctions.removeCity(username,city)
        }
    }










}