package com.sango.lunchi.splash

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.sango.core.model.AccessToken
import com.sango.core.util.ApiErrorResponse
import com.sango.core.util.ApiResponse
import com.sango.core.util.ApiSuccessResponse
import com.sango.lunchi.R
import com.sango.lunchi.databinding.ActivitySplashBinding
import com.sango.lunchi.restaurantslist.RestaurantsListActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Here we set the content view
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        //Here we se the our viewModel instance
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        binding.viewModel = viewModel

        //Here we request our access token to the api
        viewModel.accessTokenRepository.getAccessToken(
            getString(R.string.client_id),
            getString(R.string.client_secret)
        ).observe(this, getAccessTokenObserver())
    }

    /**
     * Return an observable to handle the access token
     * request from the API
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAccessTokenObserver(): Observer<ApiResponse<AccessToken>> = Observer { response ->
        when (response) {
            is ApiSuccessResponse -> {
                //Save the new token
                viewModel.accessTokenRepository.insertToken(response.body)

                //Open the restaurant activity
                startActivity(RestaurantsListActivity.getNewInstance(this))
                finish()
            }
            is ApiErrorResponse -> {
                viewModel.errorVisibility.set(View.VISIBLE)
            }
        }
    }
}
