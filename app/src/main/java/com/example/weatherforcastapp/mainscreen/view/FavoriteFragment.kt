package com.example.weatherforcastapp.mainscreen.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherforcastapp.R
import com.google.android.material.tabs.TabLayout


class FavoriteFragment : Fragment() {


    lateinit var tabLayout : TabLayout
    lateinit var optionFrameLayout: FrameLayout

    companion object{
        private var frameID : Int = 0
        fun getTheID() : Int{
            return frameID
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar!!.title = "Favorites"

        tabLayout = view.findViewById(R.id.tabLayoutForFavorite)
        optionFrameLayout = view.findViewById(R.id.optionsContainerForFav)

        frameID = optionFrameLayout.id

        // go to the favourite fragment
        parentFragmentManager.beginTransaction().replace(optionFrameLayout.id,
            FavoriteCitiesFragment()
        ).commit()

        // set on click for the tab layout

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if(tab.position == 0){
                    println("we are in the fav tab")
                    parentFragmentManager.beginTransaction().replace(optionFrameLayout.id, FavoriteCitiesFragment() ).commit()
                }else if(tab.position == 1){
                    println("we are in the manage tab")
                    parentFragmentManager.beginTransaction().replace(optionFrameLayout.id, FavoriteManageFragment() ).commit()
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