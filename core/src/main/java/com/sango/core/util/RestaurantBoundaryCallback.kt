package com.sango.core.util

import android.arch.paging.PagedList
import android.support.annotation.VisibleForTesting
import com.sango.core.db.RestaurantDao
import com.sango.core.model.Restaurant
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class RestaurantBoundaryCallback(
    private val dao: RestaurantDao,
    private val onRequestRestaurants: (offset: Int) -> Unit,
    private val ioExecutor: Executor = Executors.newSingleThreadExecutor()
) : PagedList.BoundaryCallback<Restaurant>() {

    // keep the last requested offset. When the request is successful, increment the page number.
    private var lastRequestedOffset = 0

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    //Total elements
    private var totalElements = 0

    override fun onZeroItemsLoaded() {
        requestAndSaveData()
    }

    override fun onItemAtEndLoaded(itemAtEnd: Restaurant) {
        requestAndSaveData()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun requestAndSaveData() {
        if (isRequestInProgress) return

        isRequestInProgress = true

        // Here we request the data in the lifecycle owner
        onRequestRestaurants(lastRequestedOffset)
    }

    /**
     * Insert the new data in the local storage
     * @param restaurants new requested restaurants
     */
    fun updateRestaurants(restaurants: List<Restaurant>?) {
        ioExecutor.execute {
            dao.insertRestaurants(restaurants)
            lastRequestedOffset += 20
            isRequestInProgress = false
        }
    }

    /**
     * Reset the flags
     */
    fun resetFlags() {
        isRequestInProgress = false
    }
}