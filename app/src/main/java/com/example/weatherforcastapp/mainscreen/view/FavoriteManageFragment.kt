package com.example.weatherforcastapp.mainscreen.view

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.database.Repo
import com.example.weatherforcastapp.mainscreen.viewmodel.ViewModelForMainScreen
import com.example.weatherforcastapp.mainscreen.viewmodel.factory
import com.example.weatherforcastapp.model.Cities
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FavoriteManageFragment : Fragment() , OnMapReadyCallback {


    lateinit var mapView: MapView
    lateinit var map: GoogleMap

    lateinit var viewModel : ViewModelForMainScreen


    private lateinit var mAutoCompleteAdapter: AutoCompleteTextView

    private  lateinit var addButton : Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_favorite_manage, container, false)

        // connect this with his view model
        val repo = Repo()
        val factory = factory(repo)
        viewModel = ViewModelProvider(this,factory)[ViewModelForMainScreen::class.java]

        mapView = view.findViewById(R.id.mapView2)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        mapView.getMapAsync{

            it.setOnMapLongClickListener { it ->
                println("long pressed")

                val lat = it.latitude
                val lng = it.longitude
                var cityAr = viewModel.getTheCityName(lat,lng,"ar",this.requireActivity())
                var cityEn = viewModel.getTheCityName(lat,lng,"en",this.requireActivity())

                val cityToAdd = Cities(MainScreen.getTheCurrentUser().username,lat,lng,cityAr,cityEn)


                val city = LatLng(lat,lng)



                map.addMarker(MarkerOptions().position(city).title("Here"))

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(city,4f));
                // Zoom in, animating the camera.
                map.animateCamera(CameraUpdateFactory.zoomIn())
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                map.animateCamera(CameraUpdateFactory.zoomTo(4F), 2000, null);

                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.manageTheFavoriteCities(cityToAdd)
                }


            }



        }



        mAutoCompleteAdapter = view.findViewById(R.id.autoCompleteTextView)
        //Creating the instance of ArrayAdapter containing list of language names
        //Creating the instance of ArrayAdapter containing list of language names

        //val language = arrayOf("cairo", "alexandria", "Java", ".NET", "iPhone", "Android", "ASP.NET", "PHP")

        val res: Resources = resources
        val planets = res.getStringArray(R.array.countries_array)

        val adapter: ArrayAdapter<String> = ArrayAdapter(this.requireContext(),android.R.layout.select_dialog_item,planets)
        //Getting the instance of AutoCompleteTextView
        //Getting the instance of AutoCompleteTextView



        mAutoCompleteAdapter.threshold = 1 //will start working from first character

        mAutoCompleteAdapter.setAdapter(adapter) //setting the adapter data into the AutoCompleteTextView

        mAutoCompleteAdapter.setTextColor(Color.WHITE)

        mAutoCompleteAdapter.dropDownHeight = LinearLayout.LayoutParams.WRAP_CONTENT


        addButton = view.findViewById(R.id.addCityButton)

        addButton.setOnClickListener {

            if(mAutoCompleteAdapter.text.toString().trim().isNotEmpty()){

                val point : GeoPoint? = viewModel.getLocationFromAddress(mAutoCompleteAdapter.text.toString(),this.requireActivity())
                if (point!=null){

                    // we have then city info then we need to save it into favorite
                    val cityAr = viewModel.getTheCityName(point.latitude,point.longitude,"ar",this.requireActivity())
                    val cityEn = viewModel.getTheCityName(point.latitude,point.longitude,"en",this.requireActivity())
                    val cityToAdd = Cities(MainScreen.getTheCurrentUser().username,point.latitude,point.longitude,cityAr,cityEn)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.manageTheFavoriteCities(cityToAdd)
                    }

                }

            }



        }

        viewModel.manageFavoriteAnswer.observe(viewLifecycleOwner){

            // now we need to go to the favorite list page

            if(it!="failed") {
                Toast.makeText(this.requireContext(), "Added to favorite", Toast.LENGTH_LONG).show()
                mAutoCompleteAdapter.text.clear()
            }else{
                Toast.makeText(this.requireContext(), "failed to add this location", Toast.LENGTH_LONG).show()
            }

        }

        return view
    }



    override fun onResume() {
        super.onResume()
        mapView.onResume()

    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(p0: GoogleMap) {

        map = p0
        map.uiSettings.isMyLocationButtonEnabled = false

        mapView.clearAnimation()
        map.clear()
        map.resetMinMaxZoomPreference()

        val city = LatLng(0.0,0.0)

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(city,2f));
        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomIn())
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        map.animateCamera(CameraUpdateFactory.zoomTo(2F), 2000, null)


    }


}