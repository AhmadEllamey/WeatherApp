package com.example.weatherforcastapp.mainscreen.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.example.weatherforcastapp.MainActivity
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.database.Repo
import com.example.weatherforcastapp.model.UserInfo
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class MainScreen : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener ,
    GoogleApiClient.OnConnectionFailedListener {


    lateinit var fragmentManager: FragmentManager

    lateinit var frameLayout: FrameLayout

    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var navigationView: NavigationView


    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var gso: GoogleSignInOptions

    lateinit var headerName: TextView


    companion object{

        private lateinit var currentUnit : String
        private lateinit var userInfo: UserInfo
        private lateinit var activityPointer : Activity
        fun getTheCurrentUser():UserInfo{
            return userInfo
        }
        fun getTheCurrentUnit(): String{
            val mPrefs: SharedPreferences = activityPointer.getPreferences(MODE_PRIVATE)
            currentUnit = mPrefs.getString("CurrentUnit","NA")!!
            return currentUnit
        }

        fun setTheCurrentUnit(unit : String){
            currentUnit = unit
            val mPrefs: SharedPreferences = activityPointer.getPreferences(MODE_PRIVATE)
            val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
            prefsEditor.putString("CurrentUnit", currentUnit)
            prefsEditor.apply()
            prefsEditor.commit()
        }
    }


    fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.getConfiguration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.getDisplayMetrics())
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get the object from the bundle

        userInfo = intent.getSerializableExtra("User") as UserInfo

        if(userInfo.language == "Arabic"){
            setLocale(this,"ar")
        }else if(userInfo.language == "English"){
            setLocale(this,"en")
        }

        setContentView(R.layout.activity_main_screen)

        frameLayout = findViewById(R.id.mainContainer)

        activityPointer = this


        // manage the gmail and firebase options
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()





        drawerLayout = findViewById(R.id.my_drawer_layout)
        navigationView = findViewById(R.id.menuItems)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        navigationView.setNavigationItemSelectedListener(this)



        headerName = navigationView.getHeaderView(0).findViewById(R.id.usernameForHeader)
        headerName.text = userInfo.username

        // todo --> go to a home fragment
        // for testing settings we will move to settings for now

        fragmentManager = supportFragmentManager
        frameLayout = findViewById(R.id.mainContainer)
        supportFragmentManager.beginTransaction().replace(frameLayout.id, HomeFragment()).commit()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().replace(frameLayout.id, HomeFragment()).commit()
            }
            R.id.nav_settings -> {
                supportFragmentManager.beginTransaction().replace(frameLayout.id, SettingsFragment()).commit()
            }
            R.id.nav_favorite -> {
                supportFragmentManager.beginTransaction().replace(frameLayout.id, FavoriteFragment()).commit()
            }
            R.id.nav_alert -> {
                supportFragmentManager.beginTransaction().replace(frameLayout.id, AlertsFragment()).commit()
            }
            R.id.nav_log_out -> {
                // todo -- > log out from the user

                // Firebase sign out

                // Firebase sign out
                FirebaseAuth.getInstance().signOut()

                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback { status ->
                    if (status.isSuccess) {
                        Toast.makeText(applicationContext, "Session closed", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toast.makeText(applicationContext, "Session not close", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                // move to the login screen

                // move to the login screen
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)


            }
        }


        navigationView.setCheckedItem(item.itemId)

        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }


    override fun onStart() {
        super.onStart()
        Objects.requireNonNull(supportActionBar!!).setDisplayHomeAsUpEnabled(true)
        val opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient)
        if (opr.isDone) {
            val result = opr.get()
            //handleSignInResult(result);
        } else {
            opr.setResultCallback {
                //handleSignInResult(googleSignInResult);
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed();

        // ToDO -- > handle what to do when back button pressed ...
    }


    override fun onConnectionFailed(p0: ConnectionResult) {

    }

}