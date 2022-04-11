package com.example.weatherforcastapp.mainscreen.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.model.Daily
import java.text.DateFormatSymbols
import java.util.*

class DaysAdapter (var daysList: List<Daily>) :
    RecyclerView.Adapter<DaysAdapter.ViewHolder>() {


    class ViewHolder(var layout: View) : RecyclerView.ViewHolder(
        layout
    ) {
        var dayText: TextView = itemView.findViewById(R.id.dayText)
        var tempText: TextView = itemView.findViewById(R.id.tempDetailsText)
        var descText: TextView = itemView.findViewById(R.id.descText)
        var tempIcon: ImageView = itemView.findViewById(R.id.stateDetailsIcon)

    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v: View = layoutInflater.inflate(R.layout.singlerowdaily, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val degreeShape = when (MainScreen.getTheCurrentUser().temperatureUnit) {
            "Kelvin" -> {
                "K"
            }
            "Fahrenheit" -> {
                "°F"
            }
            else -> {
                "°C"
            }
        }


        if(HomeFragment.getTheMode() == "Offline"){

            if(MainScreen.getTheCurrentUnit() == MainScreen.getTheCurrentUser().temperatureUnit){
                holder.tempText.text = "${daysList[position].temp.min.toDouble().toInt()}" +
                        "/${daysList[position].temp.max.toDouble().toInt()}$degreeShape"
            }else {
                println(MainScreen.getTheCurrentUnit())
                println(MainScreen.getTheCurrentUser().temperatureUnit)

                if (MainScreen.getTheCurrentUnit() == "Kelvin" && MainScreen.getTheCurrentUser().temperatureUnit == "Celsius"){
                    holder.tempText.text = "${(daysList[position].temp.min.toDouble() -273.15).toInt()}" +
                            "/${(daysList[position].temp.max.toDouble() -273.15).toInt()}$degreeShape"
                }else if (MainScreen.getTheCurrentUnit() == "Kelvin" && MainScreen.getTheCurrentUser().temperatureUnit == "Fahrenheit"){
                    holder.tempText.text = "${((daysList[position].temp.min.toDouble() - 273.15) *(9/5) + 32 ).toInt()}" +
                            "/${((daysList[position].temp.max.toDouble() - 273.15) *(9/5) + 32 ).toInt()}$degreeShape"
                }else if (MainScreen.getTheCurrentUnit() == "Celsius" && MainScreen.getTheCurrentUser().temperatureUnit == "Kelvin"){
                     holder.tempText.text = "${(daysList[position].temp.min.toDouble() +273.15).toInt()}" +
                            "/${(daysList[position].temp.max.toDouble() +273.15).toInt()}$degreeShape"
                }else if (MainScreen.getTheCurrentUnit() == "Celsius" && MainScreen.getTheCurrentUser().temperatureUnit == "Fahrenheit"){
                     holder.tempText.text = "${((daysList[position].temp.min.toDouble() * (9/5)) + 32 ).toInt()}" +
                            "/${((daysList[position].temp.max.toDouble() * (9/5)) + 32 ).toInt()}$degreeShape"
                }else if (MainScreen.getTheCurrentUnit() == "Fahrenheit" && MainScreen.getTheCurrentUser().temperatureUnit == "Kelvin"){
                    holder.tempText.text = "${((daysList[position].temp.min.toDouble() - 32) *(5/9) + 273.15 ).toInt()}" +
                            "/${((daysList[position].temp.max.toDouble() - 32) *(5/9) + 273.15 ).toInt()}$degreeShape"
                }else if (MainScreen.getTheCurrentUnit() == "Fahrenheit" && MainScreen.getTheCurrentUser().temperatureUnit == "Celsius"){
                    holder.tempText.text = "${((daysList[position].temp.min.toDouble() - 32) *(5/9) ).toInt()}" +
                            "/${((daysList[position].temp.max.toDouble() - 32) *(5/9) ).toInt()}$degreeShape"

                }
            }

        }else{
            holder.tempText.text = "${daysList[position].temp.min.toDouble().toInt()}" +
                    "/${daysList[position].temp.max.toDouble().toInt()}$degreeShape"
        }



        when (daysList[position].weather[0].mainText) {
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

        holder.descText.text = daysList[position].weather[0].description

        when (position) {
            1 -> {
                if(MainScreen.getTheCurrentUser().language == "English"){
                    holder.dayText.text = "Tomorrow"
                }else{
                    holder.dayText.text = "الغد"
                }

            }
            0 -> {
                if(MainScreen.getTheCurrentUser().language == "English"){
                    holder.dayText.text = "Today"
                }else{
                    holder.dayText.text = "اليوم"
                }
            }
            else -> {
                val ts = daysList[position].dt.toLong()
                val date = Date(ts*1000)

                val language =  if (MainScreen.getTheCurrentUser().language=="Arabic"){
                    "ar"
                }else {
                    "en"
                }
                val locale = Locale(language)
                //val df: DateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale)
                //println(df.format(date))
                //val dayWeekText = SimpleDateFormat("EEE").format(date)
                holder.dayText.text = getDayName(date.day,locale)
            }
        }


    }

    private fun getDayName(day: Int, locale: Locale?): String {
        val symbols = DateFormatSymbols(locale)
        val dayNames: Array<String> = symbols.weekdays
        return dayNames[day+1]
    }

    override fun getItemCount(): Int {
        return daysList.size
    }
}