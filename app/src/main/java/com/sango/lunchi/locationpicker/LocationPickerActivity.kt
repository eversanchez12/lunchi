package com.sango.lunchi.locationpicker

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sango.lunchi.R

class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val ARG_LATITUDE = "latitude"
        const val ARG_LONGITUDE = "longitude"

        /**
         * Return a single instance from Location Picker
         * activity
         * @param context application context
         * @param lat latitude
         * @param lng longitude
         */
        fun newInstance(context : Context, lat: Double, lng: Double) =
            Intent(context,LocationPickerActivity::class.java).apply {
                putExtra(ARG_LATITUDE,lat)
                putExtra(ARG_LONGITUDE,lng)
            }
    }

    private lateinit var mMap: GoogleMap
    private var selectedLat = 0.0
    private var selectedLng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home->onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //Here we init our map
        mMap = googleMap
        mMap.setOnMapClickListener {
            mMap.clear()
            setCurrentLocationMarker(it)
        }

        //Get the current location
        selectedLat = intent.getDoubleExtra(ARG_LATITUDE,0.0)
        selectedLng = intent.getDoubleExtra(ARG_LONGITUDE,0.0)
        val currentLocation = LatLng(selectedLat, selectedLng)

        //Here we update the location in the map
        setCurrentLocationMarker(currentLocation)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15f))

    }

    /**
     * Set the marker in the current location
     * @param currentLocation the current location
     */
    private fun setCurrentLocationMarker(currentLocation : LatLng){
        mMap.addMarker(MarkerOptions().position(currentLocation).title(getString(R.string.current_location)))
    }
}
