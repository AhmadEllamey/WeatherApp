package com.example.weatherforcastapp.mainscreen.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.database.Repo
import com.example.weatherforcastapp.mainscreen.viewmodel.ViewModelForMainScreen
import com.example.weatherforcastapp.mainscreen.viewmodel.factory
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton


class SettingsLocationFragment : Fragment() , OnMapReadyCallback {


    lateinit var setCurrentLocationButton : FloatingActionButton

    lateinit var mapView: MapView
    lateinit var map: GoogleMap


    var wayLatitude = 0.0
    var wayLongitude = 0.0
    lateinit var fusedLocationClient: FusedLocationProviderClient
    var locationRequest: LocationRequest = LocationRequest.create()

    lateinit var viewModel : ViewModelForMainScreen


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings_location, container, false)


        setCurrentLocationButton = view.findViewById(R.id.floatingButton)

        setCurrentLocationButton.setOnClickListener {

            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this.requireActivity())

                if ((ContextCompat.checkSelfPermission(this.requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) ===
                            PackageManager.PERMISSION_GRANTED)) {

                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        mLocationCallback,
                        Looper.getMainLooper()
                    )
                }



        }



        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this.requireActivity())


        // connect this with his view model
        val repo = Repo()
        val factory = factory(repo)
        viewModel = ViewModelProvider(this,factory)[ViewModelForMainScreen::class.java]

        // Gets the MapView from the XML layout and creates it
        // Gets the MapView from the XML layout and creates it
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


        mapView.getMapAsync{

            it.setOnMapLongClickListener { it ->
                println("long pressed")

                MainScreen.getTheCurrentUser().lat = it.latitude.toString()
                MainScreen.getTheCurrentUser().longtude = it.longitude.toString()

                val city = LatLng(MainScreen.getTheCurrentUser().lat.toDouble(),
                    MainScreen.getTheCurrentUser().longtude.toDouble())

                mapView.clearAnimation()
                map.clear()
                map.resetMinMaxZoomPreference()

                map.addMarker(MarkerOptions().position(city).title("Here"))

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(city,15f));
                // Zoom in, animating the camera.
                map.animateCamera(CameraUpdateFactory.zoomIn())
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                map.animateCamera(CameraUpdateFactory.zoomTo(15F), 2000, null);

                viewModel.updateUser(MainScreen.getTheCurrentUser())

            }



        }

        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY;


        return view
    }


    override fun onResume() {
        mapView.onResume()
        super.onResume()

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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {

        println("we are here ***********************************************")


        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this.requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                        println("permission granted")

                        lateinit var city : LatLng

                        if (MainScreen.getTheCurrentUser().longtude != "NA"
                            && MainScreen.getTheCurrentUser().lat != "NA"
                        ){
                            city = LatLng(MainScreen.getTheCurrentUser().lat.toDouble(),
                                MainScreen.getTheCurrentUser().longtude.toDouble())

                            map.clear()
                            map.resetMinMaxZoomPreference()
                            mapView.clearAnimation()
                            map.addMarker(MarkerOptions().position(city).title("Here"))

                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(city,15f));
                            // Zoom in, animating the camera.
                            map.animateCamera(CameraUpdateFactory.zoomIn());
                            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                            map.animateCamera(CameraUpdateFactory.zoomTo(15F), 2000, null);


                        }else {
                            // get the current location and set it in the map

                            fusedLocationClient =
                                LocationServices.getFusedLocationProviderClient(this.requireActivity())

                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                mLocationCallback,
                                Looper.getMainLooper()
                            )


                        }

                    }
                } else {
                    println("Refused *********************************")
                }
                return
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map.uiSettings.isMyLocationButtonEnabled = false
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    1
                )
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }

            return
        }
        map.isMyLocationEnabled = true





        if (MainScreen.getTheCurrentUser().longtude != "NA"
            && MainScreen.getTheCurrentUser().lat != "NA"
        ){
            val city = LatLng(MainScreen.getTheCurrentUser().lat.toDouble(),
                MainScreen.getTheCurrentUser().longtude.toDouble())


            mapView.clearAnimation()
            map.clear()
            map.resetMinMaxZoomPreference()

            map.addMarker(MarkerOptions().position(city).title("Here"))

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(city,15f));
            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomIn())
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            map.animateCamera(CameraUpdateFactory.zoomTo(15F), 2000, null)


        }else{
            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this.requireActivity())

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                mLocationCallback,
                Looper.getMainLooper()
            )
        }


        /*
       //in old Api Needs to call MapsInitializer before doing any CameraUpdateFactory call
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
       */
        // Updates the location and zoom of the MapView
        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        map.animateCamera(cameraUpdate);*/
        /*
       //in old Api Needs to call MapsInitializer before doing any CameraUpdateFactory call
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
       */
        // Updates the location and zoom of the MapView
        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        map.animateCamera(cameraUpdate);*/

    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            wayLatitude = locationResult.lastLocation.latitude
            wayLongitude = locationResult.lastLocation.longitude
            println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")

            // we now have the current location

            // todo --> update the user object
            MainScreen.getTheCurrentUser().lat = wayLatitude.toString()
            MainScreen.getTheCurrentUser().longtude = wayLongitude.toString()


            val city = LatLng(MainScreen.getTheCurrentUser().lat.toDouble(),
                MainScreen.getTheCurrentUser().longtude.toDouble())

            // todo --> save this location in the fire store
            // todo --> save this location in the room
            viewModel.updateUser(MainScreen.getTheCurrentUser())

            map.clear()
            map.resetMinMaxZoomPreference()
            mapView.clearAnimation()
            map.addMarker(MarkerOptions().position(city).title("Here"))

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(city,15f));
            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomIn())
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            map.animateCamera(CameraUpdateFactory.zoomTo(15F), 2000, null);


        }
    }

}

