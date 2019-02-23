package com.sango.lunchi.splash

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sango.lunchi.R
import com.sango.lunchi.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding
    lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Here we set the content view
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        //Here we se the our viewModel instance
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        binding.viewModel = viewModel

        //Here we request our access toke to the api

    }
}
