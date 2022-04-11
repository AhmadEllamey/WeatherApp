package com.example.weatherforcastapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforcastapp.model.Cities
import com.example.weatherforcastapp.model.UserInfo


@Database(entities = [UserInfo::class , Cities::class] , version = 1)
abstract class AppDatabase : RoomDatabase(){

    abstract fun databaseFunctions(): DatabaseFunctions?

    companion object {
        private var instance: AppDatabase? = null
        @Synchronized
        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "user")
                    .build()
            }
            return instance
        }
    }


}