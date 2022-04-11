package com.example.weatherforcastapp.mainscreen.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.database.Repo
import com.example.weatherforcastapp.mainscreen.viewmodel.ViewModelForMainScreen
import com.example.weatherforcastapp.mainscreen.viewmodel.factory
import com.example.weatherforcastapp.model.Daily
import com.example.weatherforcastapp.model.Hourly
import java.text.DateFormatSymbols
import java.text.DecimalFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var viewModel : ViewModelForMainScreen
    lateinit var currentActivity: Activity


    lateinit var cityNameText : TextView
    lateinit var dateText : TextView
    lateinit var weatherStateText : TextView
    lateinit var degreeText : TextView

    private lateinit var recyclerViewForHours: RecyclerView
    private lateinit var hoursAdapter: HoursAdapter

    lateinit var recyclerViewForDays: RecyclerView
    lateinit var daysAdapter: DaysAdapter

    lateinit var pressureText : TextView
    lateinit var humidityText : TextView
    lateinit var windSpeedText : TextView
    lateinit var cloudsText : TextView
    lateinit var ultraVioletText : TextView
    lateinit var visibilityText : TextView



    companion object{

        private lateinit var mode :String
        fun getTheMode():String{
            return mode
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar!!.title = "Home"

        cityNameText = view.findViewById(R.id.cityNameText)
        dateText = view.findViewById(R.id.dateText)
        weatherStateText = view.findViewById(R.id.weatherStateText)
        degreeText = view.findViewById(R.id.degreeText)

        pressureText = view.findViewById(R.id.pressureText)
        humidityText = view.findViewById(R.id.humidityText)
        windSpeedText = view.findViewById(R.id.windText)
        cloudsText = view.findViewById(R.id.cloudText)
        ultraVioletText = view.findViewById(R.id.uvText)
        visibilityText = view.findViewById(R.id.visibilityText)

        // set the hours recycle

        recyclerViewForHours = view.findViewById(R.id.hoursRecycle)
        recyclerViewForHours.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this.requireContext())
        layoutManager.orientation = RecyclerView.HORIZONTAL
        recyclerViewForHours.layoutManager = layoutManager
        val list : List<Hourly> = listOf()
        hoursAdapter = HoursAdapter(list)
        recyclerViewForHours.adapter = hoursAdapter

        // set the days recycle

        recyclerViewForDays = view.findViewById(R.id.daysRecycle)
        recyclerViewForDays.setHasFixedSize(true)
        val layoutManagerX = LinearLayoutManager(this.requireContext())
        layoutManagerX.orientation = RecyclerView.VERTICAL
        recyclerViewForDays.layoutManager = layoutManagerX
        val listX : List<Daily> = listOf()
        daysAdapter = DaysAdapter(listX)
        recyclerViewForDays.adapter = daysAdapter
        currentActivity = this.requireActivity()
        // my key is : 9c33271eb9374e70e2266d93e0d49785

        //api.openweathermap.org/data/2.5/onecall?
        // lat={lat}
        // &lon={lon}
        // &exclude={part} what you want to exclude from the result could be (current,minutely,hourly,daily,alerts)
        // &appid={API key} my key
        // &lang={language} used to get the result with your language ar for arabic en for english
        //For temperature in Fahrenheit and wind speed in miles/hour, use units=imperial
        //For temperature in Celsius and wind speed in meter/sec, use units=metric
        //Temperature in Kelvin and wind speed in meter/sec is used by default,
        // so there is no need to use the units parameter in the API call if you want this


        // attach to the model view
        val repo = Repo()
        val factory = factory(repo)
        viewModel = ViewModelProvider(this,factory)[ViewModelForMainScreen::class.java]

        mode = viewModel.onlineStateIndicator

        // we need now to send a request with the user info
        var language = MainScreen.getTheCurrentUser().language
        if (language == "Arabic"){
            language = "ar"
        }else{
            language = "en"
        }

        var userUnit = MainScreen.getTheCurrentUser().temperatureUnit

        userUnit = when (userUnit) {
            "Celsius" -> {
                "metric"
            }
            "Fahrenheit" -> {
                "imperial"
            }
            else -> {
                ""
            }
        }

        viewModel.sendUpdateWeatherStateRequest(MainScreen.getTheCurrentUser().lat
            ,MainScreen.getTheCurrentUser().longtude,language,userUnit,currentActivity)


        viewModel.serverAnswer.observe(viewLifecycleOwner){


            // update the mode
            mode = viewModel.onlineStateIndicator

            // we got an answer from the server or locally from the shared preference

            // get the address from the latLng

            val mPrefs: SharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

            if(MainScreen.getTheCurrentUser().language == "Arabic"){
                cityNameText.text =  mPrefs.getString("Arabic", "NA")
            }else {
                cityNameText.text =  mPrefs.getString("English", "NA")
            }


            // get the date

            val ts = it.current.dt.toLong()
            val date = Date(ts*1000)
            val cal = Calendar.getInstance()
            cal.time = date
            println(date)
            val aLocale: Locale = Locale.Builder().setLanguage(language).build()
            val dfs = DateFormatSymbols(aLocale)
            val weekdays: Array<String> = dfs.weekdays
            val months: Array<String> = dfs.shortMonths

            dateText.text = "${weekdays[cal.get(Calendar.DAY_OF_WEEK)]} ,${cal.get(Calendar.DAY_OF_MONTH)}  ${months[cal.get(Calendar.MONTH)]}"

            // set the state

            weatherStateText.text = it.current.weather[0].description


            // set the degree
            val degreeShape : String
            if(MainScreen.getTheCurrentUser().temperatureUnit == "Kelvin"){
                degreeShape = "K"
            }else if (MainScreen.getTheCurrentUser().temperatureUnit == "Fahrenheit"){
                degreeShape = "°F"
            }else {
                degreeShape = "°C"
            }

            // check if we load the data from the device not the internet


            // set the degree

            if(viewModel.onlineStateIndicator == "Offline"){

                if(MainScreen.getTheCurrentUnit() == MainScreen.getTheCurrentUser().temperatureUnit){
                    degreeText.text = "${it.current.temp.toDouble().toInt()}$degreeShape"
                }else {
                    println(MainScreen.getTheCurrentUnit())
                    println(MainScreen.getTheCurrentUser().temperatureUnit)

                    if (MainScreen.getTheCurrentUnit() == "Kelvin" && MainScreen.getTheCurrentUser().temperatureUnit == "Celsius"){
                        degreeText.text = "${DecimalFormat("##").format(it.current.temp.toDouble() - 273.15)}$degreeShape"
                    }else if (MainScreen.getTheCurrentUnit() == "Kelvin" && MainScreen.getTheCurrentUser().temperatureUnit == "Fahrenheit"){
                        degreeText.text = "${DecimalFormat("##").format(((it.current.temp.toDouble() - 273.15)*(9/5))+32)}$degreeShape"
                    }else if (MainScreen.getTheCurrentUnit() == "Celsius" && MainScreen.getTheCurrentUser().temperatureUnit == "Kelvin"){
                        degreeText.text = "${DecimalFormat("##").format(it.current.temp.toDouble() + 273.15)}$degreeShape"
                    }else if (MainScreen.getTheCurrentUnit() == "Celsius" && MainScreen.getTheCurrentUser().temperatureUnit == "Fahrenheit"){
                        degreeText.text = "${DecimalFormat("##").format((it.current.temp.toDouble()*(9/5))+32)}$degreeShape"
                    }else if (MainScreen.getTheCurrentUnit() == "Fahrenheit" && MainScreen.getTheCurrentUser().temperatureUnit == "Kelvin"){
                        degreeText.text = "${DecimalFormat("##").format(((it.current.temp.toDouble()-32)*(5/9))+273.15)}$degreeShape"
                    }else if (MainScreen.getTheCurrentUnit() == "Fahrenheit" && MainScreen.getTheCurrentUser().temperatureUnit == "Celsius"){
                        degreeText.text = "${DecimalFormat("##").format((it.current.temp.toDouble()-32)*(5/9))}$degreeShape"
                    }
                }

            }else{
                degreeText.text = "${it.current.temp.toDouble().toInt()}$degreeShape"
            }

            // set the hours list
            hoursAdapter.hoursList = it.hourly
            hoursAdapter.notifyDataSetChanged()

            // set the days list
            daysAdapter.daysList = it.daily
            daysAdapter.notifyDataSetChanged()

            // set the last info for this screen

            visibilityText.text = it.current.visibility+" m"
            ultraVioletText.text = it.current.uvi
            cloudsText.text = it.current.clouds+" %"
            humidityText.text = it.current.humidity+" %"
            pressureText.text = it.current.pressure+" hpa"

            if(MainScreen.getTheCurrentUser().windSpeedUnit=="meter/sec"){

                if(MainScreen.getTheCurrentUser().temperatureUnit == "Celsius"
                    || MainScreen.getTheCurrentUser().temperatureUnit == "Kelvin"){

                    windSpeedText.text = it.current.wind_speed+" meter/sec"

                }else if(MainScreen.getTheCurrentUser().temperatureUnit == "Fahrenheit"){
                    // we need to convert from mile/hour to meter/sec

                    windSpeedText.text = (it.current.wind_speed.toDouble()* 0.45).toString()+" meter/sec"

                }

            }else {

                if(MainScreen.getTheCurrentUser().temperatureUnit == "Fahrenheit"){
                    windSpeedText.text = it.current.wind_speed+" mile/hour"
                }else {

                    windSpeedText.text = DecimalFormat("##.##").format((it.current.wind_speed.toDouble()* 2.23694)).toString()+" mile/hour"
                }


            }



        }



        return view
    }




}