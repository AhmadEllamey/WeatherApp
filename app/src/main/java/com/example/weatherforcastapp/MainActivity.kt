package com.example.weatherforcastapp

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.weatherforcastapp.auth.signin.view.SignInScreen
import com.example.weatherforcastapp.auth.signup.view.SignUpFragment
import com.example.weatherforcastapp.database.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {


    lateinit var fragmentManager: FragmentManager

    // firebase ref
    private lateinit var auth: FirebaseAuth

    companion object{
        lateinit var frameLayout: FrameLayout
        lateinit var context : Context
        fun getTheContext():Context{
            return context
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this

        createNotificationChannel()


        // Initialize Firebase Auth
        auth = Firebase.auth

        // create a new user with email-password firebase option
//        auth.createUserWithEmailAndPassword("ahmadellamey1@gmail.com", "adminadmin")
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                        println("createUserWithEmail:success")
//                    val user = auth.currentUser
//                    // todo --> what happened when the user register goes right
//                    //updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    println("createUserWithEmail:failure ${task.exception}")
//                    println("createUserWithEmail:failure ${task.exception}")
//                    Toast.makeText(baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                    // todo --> what happened when the user register fails
//                    //updateUI(null)
//                }
//            }



        if (supportActionBar != null) {
            supportActionBar?.hide()
        }



        // logout from the account
        //Firebase.auth.signOut()


        // move to another fragment
        fragmentManager = supportFragmentManager
        frameLayout = findViewById(R.id.container)
        supportFragmentManager.beginTransaction().replace(frameLayout.id, SignInScreen()).commit()



    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Medical Reminder Channel"
            val desc = "Channel for alarm"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("Alarm", name, importance)
            channel.description = desc
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()

        // todo --> handle the back button
        println("clicked")
        val myFragment: SignUpFragment? =
            supportFragmentManager.findFragmentByTag("TryTag") as SignUpFragment?
        if (myFragment != null && myFragment.isVisible) {
            // add your code here
            println("clicked again")
            fragmentManager = supportFragmentManager
            frameLayout = findViewById(R.id.container)
            supportFragmentManager.beginTransaction().replace(frameLayout.id, SignInScreen()).commit()
        }

    }

}