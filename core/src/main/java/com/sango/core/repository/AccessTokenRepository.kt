package com.sango.core.repository

import com.sango.core.db.AccessTokenDao
import com.sango.core.model.AccessToken
import com.sango.core.util.Api
import com.sango.core.util.AppExecutors
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles Access token objects.
 */
@Singleton
class AccessTokenRepository @Inject constructor(
    private val accessTokenDao: AccessTokenDao,
    private val apiService: Api,
    private val appExecutors: AppExecutors = AppExecutors()
) {

    /**
     * Get an API request to obtaing an Access Token
     * @param clientId id to verify  who is requesting to the API
     * @param clientSecret secret password to authenticate a request
     */
    fun getAccessToken(clientId: String, clientSecret: String) = apiService.getAccessToken(clientId,clientSecret)

    /**
     * Get the access toke from database
     */
    fun queryAccessToken() = accessTokenDao.getAccessToken()

    /**
     * Clear the access toke from the local database
     */
    fun clearAccessToken() {
        appExecutors.diskIO().execute {
            accessTokenDao.clearAccessToken()
        }
    }

    /**
     * Insert a new access token in the database
     * @param accessToken newest access token object
     */
    fun insertToken(accessToken: AccessToken) {
        appExecutors.diskIO().execute {
            accessTokenDao.apply {
                clearAccessToken()
                insert(accessToken)
            }
        }
    }
}
