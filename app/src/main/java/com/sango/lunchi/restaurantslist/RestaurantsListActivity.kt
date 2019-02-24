package com.sango.lunchi.restaurantslist

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.sango.lunchi.R
import com.sango.lunchi.databinding.ActivityRestaurantListBinding
import com.sango.lunchi.restaurantslist.RestaurantsListViewModel.Companion.CHANGE_LOCATION_EVENT
import com.sango.lunchi.restaurantslist.RestaurantsListViewModel.Companion.RETRY_LOCATION_PERMISSION_EVENT
import kotlinx.android.synthetic.main.activity_restaurant_list.*
import org.jetbrains.anko.alert

class RestaurantsListActivity : AppCompatActivity() {

    companion object {
        var TAG = RestaurantsListActivity::class.java.name ?: ""
        const val LOCATION_PERMISSION_REQUEST_CODE = 101

        /**
         * Return a instance from the RestaurantsListActivity
         * @param context application context
         */
        fun getNewInstance(context: Context) = Intent(context, RestaurantsListActivity::class.java)
    }

    private lateinit var viewModel: RestaurantsListViewModel
    private lateinit var binding: ActivityRestaurantListBinding
    private var currentLat = 0.0
    private var currentLng = 0.0
    private var locationManager: LocationManager? = null
    //Here we define the callback when the user location is get
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            //Save the location
            currentLat = location.latitude
            currentLng = location.longitude

            //Request the restaurant
            requestRestaurants()

            //Remove the location updates
            locationManager?.removeUpdates(this)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Here we set the content view
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant_list)

        //Here we se the our viewModel instance
        viewModel = ViewModelProviders.of(this).get(RestaurantsListViewModel::class.java)
        binding.viewModel = viewModel

        //Get reference to location manager
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        //Here we set the click single event
        viewModel.clickLiveEvent.observe(this, getSingleClickEventObserver())

        //Check if we have the location permission
        checkLocationPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    requestCurrentLocation()
                } else {
                    viewModel.progressBarVisibility.set(View.INVISIBLE)
                    viewModel.errorLocationVisibility.set(View.VISIBLE)
                    animateView(tv_location_error_message)
                    animateView(bt_retry_location)

                }
            }
        }
    }

    /**
     * Return an observer to listener the click interaction
     * in the layout
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getSingleClickEventObserver(): Observer<Int> = Observer {
        when (it) {
            CHANGE_LOCATION_EVENT -> {

            }
            RETRY_LOCATION_PERMISSION_EVENT -> {
                viewModel.errorLocationVisibility.set(View.INVISIBLE)
                viewModel.progressBarVisibility.set(View.VISIBLE)
                checkLocationPermission()
            }
        }
    }


    /**
     * Check if the user grant the location permission
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                alert(R.string.location_permission_message, R.string.app_name) {
                    positiveButton(R.string.accept) {
                        requestLocationPermission()
                    }
                }.show()
            } else {
                requestLocationPermission()
            }
        } else {
            requestCurrentLocation()
        }
    }

    /**
     * Request the location permission
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Request the current user location
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun requestCurrentLocation() {
        try {
            //Here we get the last know location
            val lastKnowLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (lastKnowLocation != null) {
                //Save the location
                currentLat = lastKnowLocation.latitude
                currentLng = lastKnowLocation.longitude

                //Request restaurants
                requestRestaurants()
            } else {
                locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
            }
        } catch (e: SecurityException) {
            Log.d(TAG, e.message)
        }
    }


    /**
     * Request the nearest restaurants to the user's location
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun requestRestaurants() {
        Toast.makeText(
            this,
            "Latitud: ${currentLat} Longitud: ${currentLng}",
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Help to show our view with a fade in
     * animation
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun animateView(view: View) {
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
    }
}
