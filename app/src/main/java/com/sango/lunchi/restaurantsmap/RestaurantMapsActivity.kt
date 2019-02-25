package com.sango.lunchi.restaurantsmap

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.VisibleForTesting
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.sango.core.db.AppDb
import com.sango.core.model.Restaurant
import com.sango.core.repository.RestaurantRepository
import com.sango.core.util.CoreApp
import com.sango.lunchi.R
import com.sango.lunchi.locationpicker.LocationPickerActivity


class RestaurantMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val ARG_LATITUDE = "latitude"
        private const val ARG_LONGITUDE = "longitude"

        /**
         * Return a single instance from Restaurant Maps activity
         * @param context application context
         * @param lat latitude
         * @param lng longitude
         */
        fun newInstance(context: Context, lat: Double, lng: Double) =
            Intent(context, RestaurantMapsActivity::class.java).apply {
                putExtra(ARG_LATITUDE, lat)
                putExtra(ARG_LONGITUDE, lng)
            }
    }

    private var restaurantRepository = RestaurantRepository(
        AppDb.instance(CoreApp.instance).restaurantDao(),
        CoreApp.instance.provideRetrofit()
    )
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Here we request the restaurants from database
        restaurantRepository.queryRestaurants().observe(this, getRestaurantsObserver())
    }

    /**
     * Return an observable to listen when the restaurants
     * are got from database
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getRestaurantsObserver(): Observer<List<Restaurant>> = Observer { restaurantList ->
        restaurantList.let { list ->
            //Get the current location
            val selectedLat = intent.getDoubleExtra(LocationPickerActivity.ARG_LATITUDE, 0.0)
            val selectedLng = intent.getDoubleExtra(LocationPickerActivity.ARG_LONGITUDE, 0.0)
            val currentLocation = LatLng(selectedLat, selectedLng)

            val builderLatLng = LatLngBounds.Builder()

            // Add a current location marker
            val currentMarker =
                mMap.addMarker(MarkerOptions().position(currentLocation).title(getString(R.string.current_location)))
            currentMarker.showInfoWindow()
            builderLatLng.include(currentLocation)

            //Here we add all the marker from restaurants
            list?.forEach { restaurant ->
                //Here we get the coordinates
                val locationString = restaurant.coordinates?.split(",")
                val location = LatLng(
                    locationString?.get(0)?.toDouble() ?: 0.0,
                    locationString?.get(1)?.toDouble() ?: 0.0
                )

                //Add the marker
                mMap.addMarker(
                    MarkerOptions().position(location).title(restaurant.name)
                        .icon(bitmapDescriptorFromVector(R.drawable.ic_location_restaurants))
                )
                builderLatLng.include(location)
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builderLatLng.build(), 50))
        }
    }

    /**
     * Util method to load a custom marker from a vector resource
     */
    private fun bitmapDescriptorFromVector(@DrawableRes vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(this, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
