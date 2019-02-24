package com.sango.lunchi.restaurantslist

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.view.View
import com.sango.core.db.AppDb
import com.sango.core.repository.AccessTokenRepository
import com.sango.core.util.CoreApp
import com.sango.core.util.SingleLiveEvent

class RestaurantsListViewModel : ViewModel() {

    companion object {
        const val CHANGE_LOCATION_EVENT = 0
        const val RETRY_LOCATION_PERMISSION_EVENT = 1
    }

    var progressBarVisibility: ObservableField<Int> = ObservableField(View.VISIBLE)

    var listVisibility: ObservableField<Int> = ObservableField(View.INVISIBLE)

    var floatingButtonVisibility: ObservableField<Int> = ObservableField(View.INVISIBLE)

    var errorLocationVisibility: ObservableField<Int> = ObservableField(View.INVISIBLE)

    var errorMessageVisibility: ObservableField<Int> = ObservableField(View.INVISIBLE)

    var clickLiveEvent: SingleLiveEvent<Int> = SingleLiveEvent()

    var accessTokenRepository = AccessTokenRepository(
        AppDb.instance(CoreApp.instance).accessTokenDao(),
        CoreApp.instance.provideRetrofit()
    )

    /**
     * Listen the click event in the floating
     * button to change the current user location
     */
    fun changeLocationClick() {
        clickLiveEvent.value = CHANGE_LOCATION_EVENT
    }

    /**
     * Listen the click event in the retry
     * location button
     */
    fun retryLocationClick() {
        clickLiveEvent.value = RETRY_LOCATION_PERMISSION_EVENT
    }

    /**
     * Get the nearest restaurants using the given location
     */
    fun getRestaurants(){

    }

}