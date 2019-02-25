package com.sango.core.repository

import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.sango.core.db.RestaurantDao
import com.sango.core.model.Restaurant
import com.sango.core.model.RestaurantResponse
import com.sango.core.util.Api
import com.sango.core.util.AppExecutors
import com.sango.core.util.RestaurantBoundaryCallback
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantRepository @Inject constructor(
    private val restaurantDao: RestaurantDao,
    private val apiService: Api,
    private val appExecutors: AppExecutors = AppExecutors()
) {
    companion object {
        private const val DATABASE_PAGE_SIZE = 20
    }

    private lateinit var boundaryCallback: RestaurantBoundaryCallback
    private var countryId = 1
    private var pointString = ""

    /**
     * Get the restaurants list using paged
     * list library
     */
    fun getRestaurants(
        country: Int,
        point: String,
        onRequestRestaurants: (offset: Int) -> Unit
    ): LiveData<PagedList<Restaurant>> {

        countryId = country

        pointString = point

        boundaryCallback = RestaurantBoundaryCallback(restaurantDao, onRequestRestaurants)

        // Get data from the local cache
        val dataSourceFactory = restaurantDao.getRestaurants()

        return LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback)
            .build()
    }

    /**
     * Request the new restaurant page from network
     * @param offset the index of the first element in the next page
     */
    fun getNextStoresPage(offset: Int) = apiService.getRestaurants(countryId, pointString, offset)

    /**
     * Insert the new data in the local storage
     * @param restaurantResponse new requested stores
     */
    fun updateStoreContent(restaurantResponse: RestaurantResponse) {
        boundaryCallback.updateRestaurants(restaurantResponse)
    }

    /**
     * Reset the flags
     */
    fun resetFlags() {
        boundaryCallback.resetFlags()
    }

    /**
     * Clear the previous data from the local data base
     */
    fun clearPreviousData(){
        appExecutors.diskIO().execute {
            restaurantDao.apply {
                clearAllRestaurants()
            }
        }
    }
}