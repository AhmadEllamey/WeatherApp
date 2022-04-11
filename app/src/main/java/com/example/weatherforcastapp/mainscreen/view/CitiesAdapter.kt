package com.example.weatherforcastapp.mainscreen.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.model.Cities

class CitiesAdapter (var citiesList: List<Cities> , var toGo : ManageCitySelection) :
    RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {



    class ViewHolder(var layout: View) : RecyclerView.ViewHolder(
        layout
    ) {
        var cityName: TextView = itemView.findViewById(R.id.cityNameForFavCities)
        var constraintLayout : ConstraintLayout = itemView.findViewById(R.id.constraintForFavLayout)
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v: View = layoutInflater.inflate(R.layout.single_row_for_cities, parent, false)
        return CitiesAdapter.ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CitiesAdapter.ViewHolder, position: Int) {

        if(MainScreen.getTheCurrentUser().language=="Arabic"){
            holder.cityName.text = citiesList[position].cityAr
        }else{
            holder.cityName.text = citiesList[position].city
        }

        holder.constraintLayout.setOnLongClickListener {

            println("LongPressed")
            toGo.onLongClickFunction(citiesList[position])

            true
        }

        holder.constraintLayout.setOnClickListener {
            // switch the user to the
            println("Pressed")
            toGo.onClickFunction(citiesList[position])
        }




    }

    override fun getItemCount(): Int {
        return citiesList.size
    }
}