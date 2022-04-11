package com.example.weatherforcastapp.auth.signin.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforcastapp.MainActivity
import com.example.weatherforcastapp.database.Repo
import com.example.weatherforcastapp.model.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelForAuth (var repoRef : Repo , var auth : FirebaseAuth ) : ViewModel(){


    lateinit var actionToTake : MutableLiveData<String>
    lateinit var currentUserInfoIndicator : MutableLiveData<UserInfo>


    init {
        actionToTake = MutableLiveData()
        currentUserInfoIndicator = MutableLiveData()
    }


    fun login(username : String,password : String,auth : FirebaseAuth = this.auth){
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("signInWithEmail:success")
                    val user = auth.currentUser
                    // what happens when the login is success
                    // todo --> go to the home page
                    // when we logged in we need to get the user info from the fire store and if we were online we get the data from the room
                    theCurrentUser(username)
                } else {
                    // If sign in fails, display a message to the user.
                    actionToTake.postValue("failed to login")
                }
            }
    }

    private fun theCurrentUser(username : String) {


        println("inside the current user function")
        // try to get the current info from the fire store
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val docRef = db.collection("UsersInfo").document(username)
        var currentUser : UserInfo? = null
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    currentUser = document.toObject<UserInfo>()!!
                    currentUserInfoIndicator.postValue(currentUser!!)
                    println("DocumentSnapshot data: ${currentUser?.username}")

                    // insert the current user info into the room
                    CoroutineScope(Dispatchers.IO).launch {
                        repoRef.insertTheUser(currentUser!!)
                    }

                } else {
                    //Log.d(TAG, "No such document")
                    // todo --> insert the default info for this user then return the default user
                    val defUser = UserInfo(username,"Kelvin","meter/sec","English","NA","NA","NA")

                    // insert the info into the fire store

                    db.collection("UsersInfo").document(username).set(defUser)

                    // insert the info into the room

                    CoroutineScope(Dispatchers.IO).launch {
                        repoRef.insertTheUser(defUser)
                    }

                    // return the user info
                    currentUser = defUser
                    println("the object is ${currentUser.toString()}")
                    currentUserInfoIndicator.postValue(currentUser!!)
                }
            }
            .addOnFailureListener {
                //Log.d(TAG, "get failed with ", exception)
                //todo --> we will need to load the data from the local room
                CoroutineScope(Dispatchers.IO).launch {
                    currentUser = repoRef.getTheUser(username)
                    if(currentUser == null){
                        println("can't find your account")
                        println("the error is ----> $it")
                        actionToTake.postValue("Something went wrong")
                    }else{
                        currentUserInfoIndicator.postValue(currentUser!!)
                    }

                }
            }

    }

    fun theCurrentGmailUser(username : String){
        println("wmwmwmwmwmwmwmwmwmwmwmwmwmwmwmwmwmwmwmwmwmwmwmwm $username")
        theCurrentUser(username)
    }

    fun signUp(username : String , password : String){
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                        println("createUserWithEmail:success")
                    val user = auth.currentUser
                    // todo --> what happened when the user register goes right
                    theCurrentUser(username)
                } else {
                    // If sign in fails, display a message to the user.
                    println("createUserWithEmail:failure ${task.exception}")
                    println("createUserWithEmail:failure ${task.exception}")
                    // todo --> what happened when the user register fails
                    if(password.length<8){
                        actionToTake.postValue("password is not right")
                    }else {
                        actionToTake.postValue("try another username")
                    }
                }
            }
    }

}

@Suppress("UNCHECKED_CAST")
class factory(var repo : Repo , var auth : FirebaseAuth) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelForAuth(repo,auth) as T
    }

}