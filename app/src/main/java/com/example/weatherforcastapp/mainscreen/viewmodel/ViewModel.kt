package com.example.weatherforcastapp.mainscreen.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.weatherforcastapp.alarm.MyCoroutineWorker
import com.example.weatherforcastapp.database.Repo
import com.example.weatherforcastapp.mainscreen.view.MainScreen
import com.example.weatherforcastapp.model.APIAnswer
import com.example.weatherforcastapp.model.AlertsAnswer
import com.example.weatherforcastapp.model.Cities
import com.example.weatherforcastapp.model.UserInfo
import com.example.weatherforcastapp.network.APIClient
import com.example.weatherforcastapp.network.ManageNetwork
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class ViewModelForMainScreen (var repoRef : Repo) : ViewModel() , ManageNetwork {

    var serverAnswer : MutableLiveData<APIAnswer>

    var alertAnswer : MutableLiveData<AlertsAnswer>

    var internetAnswer : MutableLiveData<String>

    var serverAnswerForFav : MutableLiveData<List<Cities>>

    var manageFavoriteAnswer : MutableLiveData<String>
    @SuppressLint("StaticFieldLeak")
    lateinit var activity: Activity

    lateinit var onlineStateIndicator : String


    init {
        serverAnswer = MutableLiveData()
        manageFavoriteAnswer = MutableLiveData()
        serverAnswerForFav = MutableLiveData()
        internetAnswer = MutableLiveData()
        alertAnswer = MutableLiveData()
        onlineStateIndicator = "Offline"
    }

     fun updateUser(user : UserInfo){
         CoroutineScope(Dispatchers.IO).launch {

             // update fire store
             val db: FirebaseFirestore = FirebaseFirestore.getInstance()
             db.collection("UsersInfo").document(user.username).set(user)
             //update the room
             repoRef.insertTheUser(user)
         }
     }

    fun requestAlertList(activity:Activity){
        this.activity = activity
        val apiClient = APIClient(this)
        apiClient.getTheAlerts(MainScreen.getTheCurrentUser())

        // get the second language request
        val secondLanguage  = if(MainScreen.getTheCurrentUser().language == "ar"){
            "en"
        }else {
            "ar"
        }

        apiClient.getTheAlertsWithTheSecondLanguage(MainScreen.getTheCurrentUser(),secondLanguage)

    }

    fun sendUpdateWeatherStateRequest(lat : String, lng : String, language : String, unit :String, activity:Activity) {
        // send the request
        this.activity = activity
        val apiClient = APIClient(this)
        apiClient.getTheTemp(lat,lng,language,unit)

        // get the second language request
        val secondLanguage  = if(language == "ar"){
            "en"
        }else {
            "ar"
        }

        apiClient.getTheSecondLanguageInfo(lat,lng,secondLanguage,unit)

    }

    override fun onRequestSuccess(temp: APIAnswer) {
        // we got an answer then we need to tell the home fragment to load the new data


        onlineStateIndicator = "Online"

        // we need to load the address with ar and en
        getAddressName(MainScreen.getTheCurrentUser().lat.toDouble(),MainScreen.getTheCurrentUser().longtude.toDouble(),"ar")
        getAddressName(MainScreen.getTheCurrentUser().lat.toDouble(),MainScreen.getTheCurrentUser().longtude.toDouble(),"en")

        // fire an action
        serverAnswer.postValue(temp)


        // we need to save this data so we can find it when we want
        val mPrefs: SharedPreferences = activity.getPreferences(MODE_PRIVATE)
        val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(temp)
        val language = if(MainScreen.getTheCurrentUser().language == "Arabic"){
            "ar"
        }else {
            "en"
        }
        prefsEditor.putString(language, json)
        prefsEditor.apply()
        prefsEditor.commit()

    }

    suspend fun manageTheFavoriteCities(city : Cities ){

        coroutineScope {

            launch {

                // fire an indication that we have finished
                if(city.city!="NA") {
                    // add this to the firebase
                    val db = FirebaseFirestore.getInstance()
                    db.collection("cities")
                        .add(city)
                        .addOnSuccessListener {
                            println("saved")
                        }
                        .addOnFailureListener {
                            println("not yet")
                        }

                    // add this data to the room
                    repoRef.insertCity(city)
                    manageFavoriteAnswer.postValue("finished")
                }else{
                    manageFavoriteAnswer.postValue("failed")
                }

            }

        }


    }

    fun getTheCityName(latitude: Double, longitude: Double, language : String , activity:Activity) : String{
        var city = "NA"

        val geocoder: Geocoder
        val addresses: List<Address>

        val aLocale: Locale = Locale.Builder().setLanguage(language).setScript("Latn").build()
        geocoder = Geocoder(activity, aLocale)

        try {
            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            )
            val cityName = addresses[0].locality
            val country = addresses[0].countryName
            city = "$cityName ,$country"

        }catch(e: IOException){
            e.printStackTrace()
        }catch(e: IndexOutOfBoundsException){
            e.printStackTrace()
        }


        return city
    }

    private fun getAddressName(latitude: Double, longitude: Double, language : String) {
        val geocoder: Geocoder
        val addresses: List<Address>

        val aLocale: Locale = Locale.Builder().setLanguage(language).setScript("Latn").build()
        geocoder = Geocoder(this.activity, aLocale)
        try {
            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            val address =
                addresses[0].getAddressLine(0)
            // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            val knownName = addresses[0].featureName // Only if available else return NULL
            println("the city is $city")
            println("the state is $state")
            println("the address is $address")

            val mPrefs: SharedPreferences = activity.getPreferences(MODE_PRIVATE)
            val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
            if(language == "ar"){
                // save the location name into arabic place
                // we need to save this data so we can find it when we want
                prefsEditor.putString("Arabic", address)
            }else {
                // save the location name into english place
                prefsEditor.putString("English", address)
            }
            prefsEditor.apply()
            prefsEditor.commit()


        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onRequestFailed(msg: String) {

        // we have no internet connection and we need to retrieve the saved data
        onlineStateIndicator = "Offline"
        val mPrefs: SharedPreferences = activity.getPreferences(MODE_PRIVATE)
        val gson = Gson()
        val language = if(MainScreen.getTheCurrentUser().language == "Arabic"){
            "ar"
        }else {
            "en"
        }
        val json = mPrefs.getString(language, "NA")
        if(json!="NA"){
            val temp: APIAnswer = gson.fromJson(json, APIAnswer::class.java)
            serverAnswer.postValue(temp)
        }else{
            // we are having nothing to show and we are not connected to the internet
            // todo --> we need to set up an alarm to run when the internet comes alive
        }

    }

    override fun onRequestSuccessForSecondLanguage(temp: APIAnswer,language: String) {
        // save the second info
        val mPrefs: SharedPreferences = activity.getPreferences(MODE_PRIVATE)
        val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(temp)
        prefsEditor.putString(language, json)
        prefsEditor.apply()
        prefsEditor.commit()
    }

    fun getTheFavoriteListUpdate(username:String){

        CoroutineScope(Dispatchers.IO).launch {
            var list : MutableList<Cities> = mutableListOf()
            // try to get all the data from fire store for the given user name
            val db = FirebaseFirestore.getInstance()
            db.collection("cities")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { documents ->
                    // clear all data from the room
                    repoRef.clearCities(username)

                    for (document in documents) {
                        list.add(document.toObject(Cities::class.java))
                        println(document.toObject(Cities::class.java).city)
                    }
                    // post the new list
                    serverAnswerForFav.postValue(list)
                    // add all data to the room
                    repoRef.insertCities(list)
                }
                .addOnFailureListener {
                    list = repoRef.getCities(username).toMutableList()
                }

        }



    }

    fun removeFavoriteCity(city: Cities){
         // remove the city from the fire store
        val db = FirebaseFirestore.getInstance()
        val jobskill_query = db.collection("cities")
            .whereEqualTo("username",city.username)
            .whereEqualTo("lat" ,city.lat)
            .whereEqualTo("lng",city.lng)

        jobskill_query.get().addOnSuccessListener {
            it.forEach{
                it.reference.delete()
            }
        }

        // remove the city from the room

        repoRef.removeCity(city.username, city.city)

        // get the data after update

        getTheFavoriteListUpdate(city.username)


    }

    fun getLocationFromAddress(strAddress: String , activity: Activity): GeoPoint? {
        val coder = Geocoder(activity)
        val address: List<Address>?
        var p1: GeoPoint? = null
        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location = address[0]
            location.latitude
            location.longitude
            p1 = GeoPoint(
                (location.latitude),
                (location.longitude)
            )
            return p1
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun switchToThisLocation(city: Cities ,  activity: Activity){

        // check if we are not in the same location
        if(city.lat != MainScreen.getTheCurrentUser().lat.toDouble() &&
            city.lng != MainScreen.getTheCurrentUser().longtude.toDouble()){

            // check if there's internet
            if(isNetworkAvailable(activity.applicationContext)){
                //there's internet
                // switch the user location to this location
                MainScreen.getTheCurrentUser().lat = city.lat.toString()
                MainScreen.getTheCurrentUser().longtude = city.lng.toString()
                internetAnswer.postValue("done")
                updateUser(MainScreen.getTheCurrentUser())

            }else{
                // no internet we need internet for this
                internetAnswer.postValue("no internet")
            }


        }else{
            // we are in the same location now
            internetAnswer.postValue("same location")
        }


    }

    override fun onRequestFailedForSecondLanguage(msg: String) {
         // what to do when we failed to load the second language
    }

    @SuppressLint("NullSafeMutableLiveData")
    override fun onAlertRequestSuccess(alerts: AlertsAnswer?) {

        if (alerts != null) {
            alertAnswer.postValue(alerts)
            // we need to save this data so we can find it when we want
            val mPrefs: SharedPreferences = activity.getPreferences(MODE_PRIVATE)
            val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
            val gson = Gson()
            val json = gson.toJson(alerts)
            val language = if(MainScreen.getTheCurrentUser().language == "Arabic"){
                "ar"
            }else {
                "en"
            }
            prefsEditor.putString("$language-alert", json)
            prefsEditor.apply()
            prefsEditor.commit()
        }


    }

    @SuppressLint("NullSafeMutableLiveData")
    override fun onAlertRequestSuccessForTheSecondLanguage(alerts: AlertsAnswer?, language: String) {
        if (alerts != null) {
            alertAnswer.postValue(alerts)
            // we need to save this data so we can find it when we want
            val mPrefs: SharedPreferences = activity.getPreferences(MODE_PRIVATE)
            val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
            val gson = Gson()
            val json = gson.toJson(alerts)
            prefsEditor.putString("$language-alert", json)
            prefsEditor.apply()
            prefsEditor.commit()
        }
    }

    override fun onAlertRequestFailedForTheSecondLanguage(msg: String) {

    }

    override fun onAlertRequestFailed(msg: String) {

        val mPrefs: SharedPreferences = activity.getPreferences(MODE_PRIVATE)
        val gson = Gson()
        val language = if(MainScreen.getTheCurrentUser().language == "Arabic"){
            "ar"
        }else {
            "en"
        }
        val json = mPrefs.getString("$language-alert", "NA")
        if(json!="NA"){
            val alert: AlertsAnswer = gson.fromJson(json, AlertsAnswer::class.java)
            if (!alert.listOfAlerts.isNullOrEmpty()){
                alertAnswer.postValue(alert)
            }

        }else{
            // we are having nothing to show and we are not connected to the internet
            // todo --> we need to set up an alarm to run when the internet comes alive
        }

    }


    fun cancelAllAlarms(context: Context){

        // clear all previous alarms
        WorkManager.getInstance(context).cancelAllWork()

    }

    @SuppressLint("SimpleDateFormat")
    fun setTheAlarms(context: Context, fromDay : String, toDay : String){

        var from: Date = SimpleDateFormat("dd/MM/yyyy").parse(fromDay)!!
        var to  : Date = SimpleDateFormat("dd/MM/yyyy").parse(toDay)!!

        val x = Calendar.getInstance()
        x.time = from
        x[Calendar.HOUR_OF_DAY] = 21
        x[Calendar.MINUTE] = 16
        from = x.time
        println(from)
        val y = Calendar.getInstance()
        y.time = to
        y[Calendar.HOUR_OF_DAY] = 9
        to = y.time

        var startTime : Long = 0
        var setAfter  : Long = 0
        var req : OneTimeWorkRequest


        var i = 0


        // prepare the user info to move


        // set the alarm to the medicine object
        val data = Data.Builder()
            .putString("username",MainScreen.getTheCurrentUser().username)
            .putString("lat",MainScreen.getTheCurrentUser().lat)
            .putString("lng",MainScreen.getTheCurrentUser().longtude)
            .putString("language",MainScreen.getTheCurrentUser().language)
            .putString("city",MainScreen.getTheCurrentUser().city)
            .putString("temp",MainScreen.getTheCurrentUser().temperatureUnit)
            .putString("wind",MainScreen.getTheCurrentUser().windSpeedUnit)
            .build()


        // clear all previous alarms
        WorkManager.getInstance(context).cancelAllWork()

        while (from.before(to)){

            startTime = from.time
            setAfter = startTime - System.currentTimeMillis()

            if(setAfter > 0){
                req = OneTimeWorkRequest.Builder(MyCoroutineWorker::class.java)
                    .setInitialDelay(setAfter, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .build()
                WorkManager.getInstance(context).enqueue(req)
                i++
                println("alarm added $i")
                println(from)
            }

            // increase the day
            val c = Calendar.getInstance()
            c.time = from
            c.add(Calendar.DATE, 1)
            c[Calendar.HOUR_OF_DAY] = 21
            x[Calendar.MINUTE] = 16
            from = c.time

        }

    }

}



@Suppress("UNCHECKED_CAST")
class factory(var repo : Repo ) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelForMainScreen(repo) as T
    }

}