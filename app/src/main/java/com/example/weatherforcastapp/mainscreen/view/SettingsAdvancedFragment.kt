package com.example.weatherforcastapp.mainscreen.view

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.database.Repo
import com.example.weatherforcastapp.mainscreen.viewmodel.ViewModelForMainScreen
import com.example.weatherforcastapp.mainscreen.viewmodel.factory
import java.util.*


class SettingsAdvancedFragment : Fragment() , View.OnClickListener {


    private lateinit var arabic : RadioButton
    private lateinit var english : RadioButton
    private lateinit var kelvin : RadioButton
    private lateinit var celsius : RadioButton
    private lateinit var fahrenheit : RadioButton
    private lateinit var milesHour : RadioButton
    private lateinit var meterSec : RadioButton

    lateinit var viewModel : ViewModelForMainScreen


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings_advanced, container, false)

        arabic = view.findViewById(R.id.arabicRadioButton)
        english = view.findViewById(R.id.englishRadioButton)
        kelvin = view.findViewById(R.id.kelvinRadioButton)
        celsius = view.findViewById(R.id.celsiusRadioButton)
        fahrenheit = view.findViewById(R.id.fahrenheitRadioButton)
        milesHour = view.findViewById(R.id.milesHourRadioButton)
        meterSec = view.findViewById(R.id.meterSecRadioButton)


        when (MainScreen.getTheCurrentUser().temperatureUnit) {
            "Kelvin" -> {
                kelvin.isChecked = true
            }
            "Celsius" -> {
                celsius.isChecked = true
            }
            else -> {
                fahrenheit.isChecked = true
            }
        }

        when (MainScreen.getTheCurrentUser().language) {
            "Arabic" -> {
                arabic.isChecked = true
            }
            "English" -> {
                english.isChecked = true
            }

        }

        when (MainScreen.getTheCurrentUser().windSpeedUnit) {
            "meter/sec" -> {
                meterSec.isChecked = true
            }
            "miles/hour" -> {
                milesHour.isChecked = true
            }
        }


        // connect this with his view model
        val repo = Repo()
        val factory = factory(repo)
        viewModel = ViewModelProvider(this,factory)[ViewModelForMainScreen::class.java]


        // todo --> set on click for the radio buttons

        arabic.setOnClickListener(this)
        english.setOnClickListener(this)
        kelvin.setOnClickListener(this)
        celsius.setOnClickListener(this)
        fahrenheit.setOnClickListener(this)
        milesHour.setOnClickListener(this)
        meterSec.setOnClickListener(this)


        return view
    }

    fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.getConfiguration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.getDisplayMetrics())
    }

    override fun onClick(v: View?) {
        when (v?.id){

            arabic.id -> {
                MainScreen.getTheCurrentUser().language = "Arabic"
                setLocale(requireActivity(),"ar")
            }

            english.id -> {
                MainScreen.getTheCurrentUser().language = "English"
                setLocale(requireActivity(),"en")
            }

            kelvin.id -> {
                MainScreen.getTheCurrentUser().temperatureUnit = "Kelvin"
            }

            celsius.id -> {
                MainScreen.getTheCurrentUser().temperatureUnit = "Celsius"
            }

            fahrenheit.id -> {
                MainScreen.getTheCurrentUser().temperatureUnit = "Fahrenheit"
            }

            milesHour.id -> {
                MainScreen.getTheCurrentUser().windSpeedUnit = "miles/hour"
            }

            meterSec.id -> {
                MainScreen.getTheCurrentUser().windSpeedUnit = "meter/sec"
            }
        }

        viewModel.updateUser(MainScreen.getTheCurrentUser())


    }


}