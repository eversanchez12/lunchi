package com.sango.lunchi.restaurantslist

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import com.sango.core.model.AccessToken
import com.sango.core.model.Restaurant
import com.sango.core.model.RestaurantResponse
import com.sango.core.util.ApiErrorResponse
import com.sango.core.util.ApiResponse
import com.sango.core.util.ApiSuccessResponse
import com.sango.lunchi.R
import com.sango.lunchi.databinding.ActivityRestaurantListBinding
import com.sango.lunchi.locationpicker.LocationPickerActivity
import com.sango.lunchi.restaurantslist.RestaurantsListViewModel.Companion.CHANGE_LOCATION_EVENT
import com.sango.lunchi.restaurantslist.RestaurantsListViewModel.Companion.RETRY_LOCATION_PERMISSION_EVENT
import com.sango.lunchi.restaurantsmap.RestaurantMapsActivity
import kotlinx.android.synthetic.main.activity_restaurant_list.*
import org.jetbrains.anko.alert


class RestaurantsListActivity : AppCompatActivity() {

    companion object {
        var TAG = RestaurantsListActivity::class.java.name ?: ""
        const val LOCATION_PERMISSION_REQUEST_CODE = 101
        const val LOCATION_PICKER_REQUEST_CODE = 102

        /**
         * Return a instance from the RestaurantsListActivity
         * @param context application context
         */
        fun getNewInstance(context: Context) = Intent(context, RestaurantsListActivity::class.java)
    }

    private lateinit var viewModel: RestaurantsListViewModel
    private lateinit var binding: ActivityRestaurantListBinding
    private lateinit var currentAccessToken: AccessToken
    private lateinit var adapter: RestaurantListAdapter
    private var currentLat = 0.0
    private var currentLng = 0.0
    private var locationManager: LocationManager? = null

    //Here we define the callback when the user location is get
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            //Save the location
            currentLat = location.latitude
            currentLng = location.longitude

            //Request the access token
            viewModel.accessTokenRepository.queryAccessToken()
                .observe(this@RestaurantsListActivity, getAccessTokenObserver())

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

        //Here we init the adapter
        adapter = RestaurantListAdapter()
        rv_restaurants.adapter = adapter

        //Check if we have the location permission
        checkLocationPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_map->{
                if (viewModel.listVisibility.get() == View.VISIBLE){
                    startActivity(RestaurantMapsActivity.newInstance(this,currentLat,currentLng))
                }
            }
        }
        return super.onOptionsItemSelected(item)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LOCATION_PICKER_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    currentLat = data?.getDoubleExtra(LocationPickerActivity.ARG_LATITUDE, 0.0) ?: 0.0
                    currentLng = data?.getDoubleExtra(LocationPickerActivity.ARG_LONGITUDE, 0.0) ?: 0.0

                    viewModel.listVisibility.set(View.INVISIBLE)
                    viewModel.errorMessageVisibility.set(View.INVISIBLE)
                    viewModel.progressBarVisibility.set(View.VISIBLE)
                    viewModel.restaurantRepository.clearPreviousData()
                    adapter.notifyDataSetChanged()
                    requestRestaurants()
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
                if (viewModel.progressBarVisibility.get() == View.INVISIBLE) {
                    startActivityForResult(
                        LocationPickerActivity.newInstance(this, currentLat, currentLng),
                        LOCATION_PICKER_REQUEST_CODE
                    )
                }
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

                //Request the access token
                viewModel.accessTokenRepository.queryAccessToken()
                    .observe(this, getAccessTokenObserver())
            } else {
                locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
            }
        } catch (e: SecurityException) {
            Log.d(TAG, e.message)
        }
    }


    /**
     * Get an observer to listen the result from
     * the current access token query
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAccessTokenObserver(): Observer<AccessToken> = Observer { accessToken ->
        accessToken?.let {
            currentAccessToken = accessToken
            requestRestaurants()
        }
    }


    /**
     * Request the nearest restaurants to the user's location
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun requestRestaurants() {
        viewModel.getRestaurants(currentAccessToken, 1, "$currentLat,$currentLng") { offset ->
            viewModel.restaurantRepository.getNextStoresPage(offset).observe(this, getRestaurantRequestObserver())
        }.observe(this, getRestaurantsPageObserver())
    }


    /**
     * Help to show our view with a fade in
     * animation
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun animateView(view: View) {
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
    }


    /**
     * Return an observer to listen the result in the request to get
     * all the restaurants
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getRestaurantRequestObserver(): Observer<ApiResponse<RestaurantResponse>> = Observer { response ->
        when (response) {
            is ApiSuccessResponse -> {
                if (response.body.total > 0) {
                    viewModel.restaurantRepository.updateStoreContent(response.body)
                } else {
                    viewModel.progressBarVisibility.set(View.INVISIBLE)
                    viewModel.errorMessage.set(getString(R.string.no_result_to_show))
                    viewModel.errorMessageVisibility.set(View.VISIBLE)
                    animateView(tv_error_message)
                    if (viewModel.floatingButtonVisibility.get() == View.INVISIBLE) {
                        viewModel.floatingButtonVisibility.set(View.VISIBLE)
                        animateView(bt_location)
                    }
                }
            }
            is ApiErrorResponse -> {
                viewModel.restaurantRepository.resetFlags()
                viewModel.progressBarVisibility.set(View.INVISIBLE)
                viewModel.errorMessage.set(getString(R.string.error_during_request))
                viewModel.errorMessageVisibility.set(View.VISIBLE)
                animateView(tv_error_message)
            }
        }
    }


    /**
     * Return an observer to listen the change in the stores from
     * database
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getRestaurantsPageObserver(): Observer<PagedList<Restaurant>> = Observer { pagedList ->
        pagedList?.let {
            if (it.isNotEmpty()) {
                adapter.submitList(it)
                viewModel.progressBarVisibility.set(View.INVISIBLE)
                if (viewModel.listVisibility.get() == View.INVISIBLE) {
                    viewModel.listVisibility.set(View.VISIBLE)
                    viewModel.floatingButtonVisibility.set(View.VISIBLE)
                    animateView(rv_restaurants)
                    animateView(bt_location)
                }
            }
        }
    }
}
