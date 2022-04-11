package com.example.weatherforcastapp.mainscreen.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import com.example.weatherforcastapp.R


class SettingsFragment : Fragment() {

    lateinit var tabLayout : TabLayout
    lateinit var optionFrameLayout: FrameLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_settings, container, false)

        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar!!.title = "Settings"

        tabLayout = view.findViewById(R.id.tabLayout)
        optionFrameLayout = view.findViewById(R.id.optionsContainer)

        // go to the location fragment
        parentFragmentManager.beginTransaction().replace(optionFrameLayout.id, SettingsLocationFragment()).commit()


        // set on click for the tab layout

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if(tab.position == 0){
                    println("we are in the location tab")
                    parentFragmentManager.beginTransaction().replace(optionFrameLayout.id, SettingsLocationFragment()).commit()
                }else if(tab.position == 1){
                    println("we are in the advanced tab")
                    parentFragmentManager.beginTransaction().replace(optionFrameLayout.id, SettingsAdvancedFragment()).commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        return view
    }





}