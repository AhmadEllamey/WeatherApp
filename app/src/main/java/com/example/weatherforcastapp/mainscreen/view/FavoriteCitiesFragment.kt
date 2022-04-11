package com.example.weatherforcastapp.mainscreen.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.database.Repo
import com.example.weatherforcastapp.mainscreen.viewmodel.ViewModelForMainScreen
import com.example.weatherforcastapp.mainscreen.viewmodel.factory
import com.example.weatherforcastapp.model.Cities
class FavoriteCitiesFragment : Fragment() , ManageCitySelection {


    lateinit var recyclerViewForCities: RecyclerView
    lateinit var citiesAdapter: CitiesAdapter

    private lateinit var viewModel : ViewModelForMainScreen


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorite_cities, container, false)

        recyclerViewForCities = view.findViewById(R.id.favoriteCitiesRecycleView)
        recyclerViewForCities.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this.requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerViewForCities.layoutManager = layoutManager
        val list : List<Cities> = listOf()
        citiesAdapter = CitiesAdapter(list , this)
        recyclerViewForCities.adapter = citiesAdapter


        // connect to the view model
        val repo = Repo()
        val factory = factory(repo)
        viewModel = ViewModelProvider(this,factory)[ViewModelForMainScreen::class.java]


        // request update the list of the favourite and observe on the flag and when the flag is ready we should fire a change

        viewModel.getTheFavoriteListUpdate(MainScreen.getTheCurrentUser().username)


        viewModel.serverAnswerForFav.observe(viewLifecycleOwner){
            println("we got to the fired flag")
            citiesAdapter.citiesList = it
            citiesAdapter.notifyDataSetChanged()
        }

        viewModel.internetAnswer.observe(viewLifecycleOwner){

            when (it) {
                "done" -> {
                    Toast.makeText(this.requireContext(),"switched to this location",Toast.LENGTH_LONG).show()
                }
                "no internet" -> {
                    Toast.makeText(this.requireContext(),"need internet for this request",Toast.LENGTH_LONG).show()
                }
                "same location" -> {
                    Toast.makeText(this.requireContext(),"you are in the same location",Toast.LENGTH_LONG).show()
                }
            }


        }


        return view
    }

    override fun onClickFunction(city : Cities) {
         viewModel.switchToThisLocation(city , this.requireActivity())
    }

    override fun onLongClickFunction(city : Cities) {
        viewModel.removeFavoriteCity(city)
    }

}