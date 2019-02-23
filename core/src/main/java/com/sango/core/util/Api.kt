package com.sango.core.util

import android.arch.lifecycle.LiveData
import com.sango.core.model.AccessToken
import retrofit2.http.POST

interface Api {

    @POST("/tokens")
    fun getAccessToken(): LiveData<ApiResponse<AccessToken>>

}
