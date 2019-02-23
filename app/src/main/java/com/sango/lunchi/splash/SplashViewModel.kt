package com.sango.lunchi.splash

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.view.View
import com.sango.core.db.AppDb
import com.sango.core.repository.AccessTokenRepository
import com.sango.core.util.CoreApp

/**
 * ViewModel to handle the interaction between
 * UI, database and activity in the splash section
 */
class SplashViewModel : ViewModel() {

    var accessTokenRepository = AccessTokenRepository(
        AppDb.instance(CoreApp.instance).accessTokenDao(),
        CoreApp.instance.provideRetrofit()
    )

    var errorVisibility: ObservableField<Int> = ObservableField(View.INVISIBLE)
}