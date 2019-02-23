package com.sango.core.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sango.core.model.AccessToken

/**
 * Interface for database access for Access
 * token related operations.
 */
@Dao
abstract class AccessTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(accessToken: AccessToken)

    @Query("SELECT * FROM AccessToken LIMIT 1")
    abstract fun getAccessToken(): LiveData<AccessToken>

    @Query("DELETE from AccessToken")
    abstract fun clearAccessToken()
}