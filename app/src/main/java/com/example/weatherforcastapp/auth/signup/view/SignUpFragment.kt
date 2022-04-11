package com.example.weatherforcastapp.auth.signup.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforcastapp.MainActivity
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.auth.signin.viewmodel.ViewModelForAuth
import com.example.weatherforcastapp.auth.signin.viewmodel.factory
import com.example.weatherforcastapp.database.Repo
import com.example.weatherforcastapp.mainscreen.view.MainScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUpFragment : Fragment() {


    lateinit var viewModel : ViewModelForAuth

    lateinit var usernameText : EditText
    lateinit var passwordText : EditText
    lateinit var rePasswordText : EditText
    lateinit var signUpButton : Button
    lateinit var termsAndConditions : CheckBox


    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val repo = Repo()
        val factory = factory(repo ,auth)
        viewModel = ViewModelProvider(this,factory)[ViewModelForAuth::class.java]

        usernameText = view.findViewById(R.id.usernameSignupTextLine)
        passwordText = view.findViewById(R.id.passwordSignUpTextLine)
        rePasswordText = view.findViewById(R.id.passwordAgainSignUpTextLine)
        signUpButton = view.findViewById(R.id.signUpButton)
        termsAndConditions = view.findViewById(R.id.termsCheckBox)


        signUpButton.setOnClickListener {

            println("SignUp clicked")
            if(usernameText.text.toString().trim().isNotEmpty()
                &&passwordText.text.toString().trim().isNotEmpty()
                &&rePasswordText.text.toString().trim().isNotEmpty()
                &&rePasswordText.text.toString().trim().equals(passwordText.text.toString().trim())
                &&termsAndConditions.isChecked){

                    println("SignUp clicked inside the function")
                viewModel.signUp(usernameText.text.toString().trim(),passwordText.text.toString().trim())

            }

        }

        viewModel.currentUserInfoIndicator.observe(viewLifecycleOwner){

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
        return view
    }

}