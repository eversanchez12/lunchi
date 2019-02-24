package com.sango.core.util

import android.arch.lifecycle.LiveData
import com.sango.core.model.AccessToken
import com.sango.core.model.RestaurantResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("tokens")
    fun getAccessToken(
        @Query("clientId") clientId: String,
        @Query("clientSecret") clientSecret: String
    ): LiveData<ApiResponse<AccessToken>>

    @GET("search/restaurants?max=20&fields=id, name, description, logo, coordinates, deliveryAreas, rating, address")
    fun getRestaurants(
        @Query("country") country: Int,
        @Query("point") point: String,
        @Query("offset") offset: Int
    ): LiveData<ApiResponse<RestaurantResponse>>

}
