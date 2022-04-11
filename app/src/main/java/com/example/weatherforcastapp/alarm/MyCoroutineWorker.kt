package com.example.weatherforcastapp.alarm

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.model.UserInfo
import com.example.weatherforcastapp.network.AlertApiClient
import kotlinx.coroutines.*


class MyCoroutineWorker(context: Context, params: WorkerParameters): CoroutineWorker(context,params) {


    var context : Context
    init {
        this.context = context
    }

    override suspend fun doWork(): Result {
        GlobalScope.launch(Dispatchers.Main) {
           println("--------------Alarm-------------")
           // get the user data
           val username = inputData.getString("username")
           val lat = inputData.getString("lat")
           val lng = inputData.getString("lng")
           val language = inputData.getString("language")
           val city = inputData.getString("city")
           val temp = inputData.getString("temp")
           val wind = inputData.getString("wind")
           val user = UserInfo(username!!,temp!!,wind!!,language!!,city!!,lat!!,lng!!)
           // what happened when the alarm gets fired
           // get the alerts for today using the user location
               val alert = AlertApiClient()
               alert.serverAlertAnswer.observeForever{

                   if(it != null){
                       // we have a data may be an empty data
                       if(it.listOfAlerts.isNotEmpty()){
                           // we have data and it's not empty
                           // todo --> we need to get the user preference to set the notification style

                           // check if the user want notifications

                           val mPrefs: SharedPreferences = applicationContext.getSharedPreferences("alertsSP",Context.MODE_PRIVATE)
                           if(mPrefs.getString("notificationRB","NA") == "Y"){

                               val builder: NotificationCompat.Builder

                               if(mPrefs.getString("notificationRB","NA") == "Y"){

                                   val soundUri: Uri =
                                       Uri.parse("android.resource://" + applicationContext.packageName + "/" + R.raw.notification)

                                   //pop up the window
                                   builder = NotificationCompat.Builder(
                                       applicationContext, "Alarm"
                                   )
                                       .setContentTitle("Weather Alert")
                                       .setContentText("check the weather alerts")
                                       .setAutoCancel(false)
                                       .setSound(soundUri)
                                       .setSmallIcon(R.drawable.alert)
                                       .setDefaults(NotificationCompat.DEFAULT_ALL)
                                       .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                               }else{

                                   //pop up the window
                                   builder = NotificationCompat.Builder(
                                       applicationContext, "Alarm"
                                   )
                                       .setContentTitle("Weather Alert")
                                       .setContentText("check the weather alerts")
                                       .setAutoCancel(false)
                                       .setSmallIcon(R.drawable.alert)
                                       .setDefaults(NotificationCompat.DEFAULT_ALL)
                                       .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                               }
                               // check if the user wants a sound with the notification


                               val notificationManagerCompat = NotificationManagerCompat.from(
                                   applicationContext
                               )
                               notificationManagerCompat.notify(123, builder.build())

                           }



                       }

                   }

                   alert.serverAlertAnswer.removeObserver{}
               }
               alert.getTheAlerts(user)

       }
        return Result.success()
    }
}