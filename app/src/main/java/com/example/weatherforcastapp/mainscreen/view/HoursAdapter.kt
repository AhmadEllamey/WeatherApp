package com.example.weatherforcastapp.mainscreen.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.model.Hourly
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class HoursAdapter (var hoursList: List<Hourly>) :
    RecyclerView.Adapter<HoursAdapter.ViewHolder>() {


    class ViewHolder(var layout: View) : RecyclerView.ViewHolder(
        layout
    ) {
        lateinit var timeText: TextView
        lateinit var tempText: TextView
        lateinit var tempIcon: ImageView

        init {
            timeText = itemView.findViewById(R.id.timeText)
            tempText = itemView.findViewById(R.id.tempText)
            tempIcon = itemView.findViewById(R.id.stateIcon)
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v: View = layoutInflater.inflate(R.layout.singlecolumn, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: HoursAdapter.ViewHolder, position: Int) {
        val ts = hoursList[position].dt.toLong()
        val date = Date(ts*1000)

        val dateFormat: DateFormat = SimpleDateFormat("hh aa")
        val dateString: String = dateFormat.format(date).toString()
        holder.timeText.text = dateString


        val degreeShape : String
        if(MainScreen.getTheCurrentUser().temperatureUnit == "Kelvin"){
            degreeShape = "K"
        }else if (MainScreen.getTheCurrentUser().temperatureUnit == "Fahrenheit"){
            degreeShape = "°F"
        }else {
            degreeShape = "°C"
        }


        if(HomeFragment.getTheMode() == "Offline"){

            if(MainScreen.getTheCurrentUnit() == MainScreen.getTheCurrentUser().temperatureUnit){
                holder.tempText.text = "${hoursList[position].temp.toDouble().toInt()}$degreeShape"
            }else {
                println(MainScreen.getTheCurrentUnit())
                println(MainScreen.getTheCurrentUser().temperatureUnit)

                if (MainScreen.getTheCurrentUnit() == "Kelvin" && MainScreen.getTheCurrentUser().temperatureUnit == "Celsius"){
                    holder.tempText.text = "${(hoursList[position].temp.toDouble() - 273.15).toInt()}$degreeShape"
                }else if (MainScreen.getTheCurrentUnit() == "Kelvin" && MainScreen.getTheCurrentUser().temperatureUnit == "Fahrenheit"){
                    holder.tempText.text = "${((((hoursList[position].temp.toDouble()- 273.15))*(9/5))+32).toInt()}$degreeShape"
                }else if (MainScreen.getTheCurrentUnit() == "Celsius" && MainScreen.getTheCurrentUser().temperatureUnit == "Kelvin"){
                    holder.tempText.text = "${(hoursList[position].temp.toDouble()+ 273.15).toInt()}$degreeShape"
                }else if (MainScreen.getTheCurrentUnit() == "Celsius" && MainScreen.getTheCurrentUser().temperatureUnit == "Fahrenheit"){
                    holder.tempText.text = "${((hoursList[position].temp.toDouble() * (9/5))+32).toInt()}$degreeShape"
                }else if (MainScreen.getTheCurrentUnit() == "Fahrenheit" && MainScreen.getTheCurrentUser().temperatureUnit == "Kelvin"){
                    holder.tempText.text = "${(((hoursList[position].temp.toDouble() - 32 ) *(5/9) ) + 273.15).toInt()}$degreeShape"
                }else if (MainScreen.getTheCurrentUnit() == "Fahrenheit" && MainScreen.getTheCurrentUser().temperatureUnit == "Celsius"){
                    holder.tempText.text = "${((hoursList[position].temp.toDouble() - 32 ) * (5/9)).toInt()}$degreeShape"
                }
            }

        }else{
            holder.tempText.text = "${hoursList[position].temp.toDouble().toInt()}$degreeShape"
        }




        when (hoursList[position].weather[0].mainText) {
            "Thunderstorm" -> {
                holder.tempIcon.setBackgroundResource(R.drawable.lightning)
            }
            "Drizzle" -> {
                holder.tempIcon.setBackgroundResource(R.drawable.hail)
            }
            "Snow" -> {
                holder.tempIcon.setBackgroundResource(R.drawable.snowflake)
            }
            "Mist" -> {
                holder.tempIcon.setBackgroundResource(R.drawable.fog)
            }
            "Smoke" -> {
                holder.tempIcon.setBackgroundResource(R.drawable.smoke)
            }
            "Haze" -> {
                holder.tempIcon.setBackgroundResource(R.drawable.haze)
            }
            "Dust" -> {
                holder.tempIcon.setBackgroundResource(R.drawable.dust)
            }
            "Fog" -> {
                holder.tempIcon.setBackgroundResource(R.drawable.fog)
            }
            "Sand" -> {
                holder.tempIcon.setBackgroundResource(R.drawable.dust)
            }
            "Squall" -> {
                holder.tempIcon.setBackgroundResource(R.drawable.dust)
            }
            "Ash" -> {
                holder.tempIcon.setBackgroundResource(R.drawable.dust)
            }
            "Tornado" -> {
                holder.tempIcon.setBackgroundResource(R.drawable.tornado)
            }
            "Clear" -> {
                holder.tempIcon.setBackgroundResource(R.drawable.sun)
            }
            "Clouds" -> {
                holder.tempIcon.setBackgroundResource(R.drawable.clouds)
            }
        }


    }

    override fun getItemCount(): Int {
        return hoursList.size
    }
}