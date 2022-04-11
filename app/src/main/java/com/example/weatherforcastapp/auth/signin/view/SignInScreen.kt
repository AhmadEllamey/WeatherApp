package com.example.weatherforcastapp.auth.signin.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforcastapp.MainActivity
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.auth.signin.viewmodel.ViewModelForAuth
import com.example.weatherforcastapp.auth.signin.viewmodel.factory
import com.example.weatherforcastapp.auth.signup.view.SignUpFragment
import com.example.weatherforcastapp.database.Repo
import com.example.weatherforcastapp.mainscreen.view.MainScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignInScreen : Fragment() {

    private lateinit var viewModel : ViewModelForAuth
    lateinit var username : EditText
    private lateinit var password : EditText
    private lateinit var signInButton : Button
    private lateinit var continueWithGmailButton : Button
    private lateinit var forgotPasswordLink : TextView
    private lateinit var signUpLink : TextView
    private lateinit var progressBar: ProgressBar

    // firebase ref
    private lateinit var auth: FirebaseAuth


    //Adding tag for logging and RC_SIGN_IN for an activity result
    private val RC_SIGN_IN = 9001

    // Adding Google sign-in client
    var mGoogleSignInClient: GoogleSignInClient? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onStart() {
        super.onStart()
    }

    private fun signInToGoogle() {
        //Calling Intent and call startActivityForResult() method
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id)
        //Calling get credential from the oogleAuthProviderG
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this.requireActivity(),
                OnCompleteListener<AuthResult?> { task ->
                    //Override th onComplete() to see we are successful or not.
                    if (task.isSuccessful) {
                        // Update UI with the sign-in user's information
                        val user = auth.currentUser
                        //Log.d(TAG, "signInWithCredential:success: currentUser: " + user!!.email)
                        Toast.makeText(
                            this.requireContext(),
                            "Continue as ${user?.email}",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.theCurrentGmailUser(user?.email!!)
                    } else {
                        // If sign-in fails to display a message to the user.
                        //Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(
                            this.requireContext(),
                            "Firebase Authentication failed:",
                            Toast.LENGTH_LONG
                        ).show()
                        println("gmail error ---->>> ${task.exception}")
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                Toast.makeText(this.context, "Google Sign in Succeeded", Toast.LENGTH_LONG).show()
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this.context, "Google Sign in Failed", Toast.LENGTH_LONG).show()
                println("the error is -----> $e")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_sign_in_screen, container, false)


        var frameLayout: FrameLayout = view.findViewById(R.id.frameLayout)
        var animation : AnimationDrawable = frameLayout.background as AnimationDrawable
        animation.start()

        // Initialize Firebase Auth
        auth = Firebase.auth


        // connect this fragment with the view model
        val repo = Repo()
        val factory = factory(repo ,auth)
        viewModel = ViewModelProvider(this,factory)[ViewModelForAuth::class.java]

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            // ToDo --> what happened when the user is already logged in
            println("user already in")
            println("the current user name : ${currentUser.email}")
            viewModel.theCurrentGmailUser(currentUser.email.toString())
        }else{
            progressBar = view.findViewById(R.id.progressBar)
            progressBar.isVisible = false
        }


        // Configuring Google Sign In
        // Configuring Google Sign In
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // for the requestIdToken, use getString(R.string.default_web_client_id), this is in the values.xml file that
                // is generated from your google-services.json file (data from your firebase project), uses the google-sign-in method
                // web api key
                .requestIdToken("455922551667-7ltjeimv9rqbd3rhsvr366t71pngj9ft.apps.googleusercontent.com") //Default_web_client_id will be matched with the
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso)



        username = view.findViewById(R.id.cityNameTextForFav)
        password = view.findViewById(R.id.passwordTextLine)
        signInButton = view.findViewById(R.id.signInButton)
        continueWithGmailButton = view.findViewById(R.id.signInWithGmailButton)
        signUpLink = view.findViewById(R.id.signUpLink)
        forgotPasswordLink = view.findViewById(R.id.forgotPasswordLink)

        // set the on click

        signInButton.setOnClickListener{
            // todo -- > action when the user enters the username and password and clicks sign in
            if(username.text.toString().trim().isNotEmpty() && password.text.toString().trim().isNotEmpty()){
                println("clicked ${username.text}")
                viewModel.login(username.text.toString().trim(),password.text.toString().trim())
            }
        }

        continueWithGmailButton.setOnClickListener {
            // todo -- > action when the user want to continue with gmail
            signInToGoogle()
        }

        signUpLink.setOnClickListener {
            // todo -- > action when the user want to sign up
            parentFragmentManager.beginTransaction().replace(MainActivity.frameLayout.id, SignUpFragment(),"TryTag").commit()

        }

        forgotPasswordLink.setOnClickListener {
            // todo --> action when the user forgot his password
            Toast.makeText(this.requireContext(),"Under Development",Toast.LENGTH_SHORT).show()
        }


        // observe on the current user
        viewModel.currentUserInfoIndicator.observe(viewLifecycleOwner){
            // todo -->  here we should redirect the user to the second activity with his info

            // put serializable object to send

            println("we are in the converter method")

            // to send data with bundle
            val bundle = Bundle()
            bundle.putSerializable("User",it)
            // go to the second activity

            val intent = Intent(MainActivity.getTheContext(), MainScreen::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

        }


        // observe on the livedata of the view media
        viewModel.actionToTake.observe(viewLifecycleOwner){
            // what happened when the data changed in this live data

            when(it){

                "failed to login" -> { showToast("Login failed" , this.requireContext()) }

                "Something went wrong" -> { showToast("Something Went Wrong" , this.requireContext()) }

                "password is not right" -> { showToast("Password is not right" , this.requireContext()) }

                "try another username" -> { showToast("Try Another Name" , this.requireContext()) }

            }


        }

        return view
    }

    fun showToast(msg : String , context: Context){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(msg : String ,view : View){
        val snackbar = Snackbar.make(view, msg,
            Snackbar.LENGTH_LONG).setAction("Action", null)
        snackbar.setActionTextColor(Color.BLUE)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.LTGRAY)
        val textView =
            snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.BLUE)
        textView.textSize = 28f
        snackbar.show()
    }


}