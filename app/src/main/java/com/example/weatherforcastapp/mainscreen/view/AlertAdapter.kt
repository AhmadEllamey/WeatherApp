package com.example.weatherforcastapp.mainscreen.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.model.Alerts



class AlertAdapter(var list : List<Alerts>) :
    RecyclerView.Adapter<AlertAdapter.ViewHolder>(){



    class ViewHolder(var layout: View) : RecyclerView.ViewHolder(
        layout
    ) {
        var desText: TextView = itemView.findViewById(R.id.alertDescriptionText)

    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v: View = layoutInflater.inflate(R.layout.alert_single_row, parent, false)
        return AlertAdapter.ViewHolder(v)
    }

    override fun onBindViewHolder(holder: AlertAdapter.ViewHolder, position: Int) {
        holder.desText.text = list[position].description
    }

    override fun getItemCount(): Int {
        return list.size
    }
}