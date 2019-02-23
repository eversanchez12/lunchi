package com.sango.core.util

import android.arch.lifecycle.LiveData
import com.sango.core.model.AccessToken
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("tokens")
    fun getAccessToken(
        @Query("clientId") clientId: String,
        @Query("clientSecret") clientSecret: String
    ): LiveData<ApiResponse<AccessToken>>

}
