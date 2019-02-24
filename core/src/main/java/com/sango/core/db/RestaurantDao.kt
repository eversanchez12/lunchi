package com.sango.core.db

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sango.core.model.Restaurant

/**
 * Interface for database access for Restaurants
 * token related operations.
 */
@Dao
interface RestaurantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRestaurants(repo: List<Restaurant>?)

    @Query("DELETE from Restaurant")
    fun clearAllRestaurants()

    @Query("SELECT * from Restaurant where name  ORDER BY name COLLATE NOCASE ASC")
    fun getRestaurants(): DataSource.Factory<Int, Restaurant>
}