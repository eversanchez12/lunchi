package com.sango.core.util

import android.app.Application
import com.parauco.core.repository.util.LiveDataCallAdapterFactory
import com.sango.core.BuildConfig
import com.sango.core.R
import com.sango.core.model.AccessToken
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * Singleton app to init variables and get a retrofit
 * instance
 */
open class CoreApp : Application() {

    companion object {
        const val HEADER_AUTHENTICATION = "Authorization"
        lateinit var instance: CoreApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }


    /**
     * Return a Api instance with the given access token to make
     * the all API request.
     * @param accessToken authorized token
     */
    fun provideRetrofit(accessToken: AccessToken? = null): Api {
        val okHttpClientBuilder = OkHttpClient.Builder()

        okHttpClientBuilder.addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            accessToken?.let { requestBuilder.header(HEADER_AUTHENTICATION, it.accessToken) }
            chain.proceed(
                requestBuilder.build()
            )
        }

        // Set Retrofit Log Level to Body in Debug builds only
        if (BuildConfig.DEBUG) {
            // Log All The Things!
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.base_url))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(NullOrEmptyConverterFactory())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClientBuilder.build())

        return retrofitBuilder.build().create(Api::class.java)
    }
}
