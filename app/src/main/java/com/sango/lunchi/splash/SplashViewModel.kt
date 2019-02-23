package com.sango.lunchi.splash

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.view.View

/**
 * ViewModel to handle the interaction between
 * UI, database and activity in the splash section
 */
class SplashViewModel : ViewModel() {

    val errorVisibility: ObservableField<Int> = ObservableField(View.INVISIBLE)

}