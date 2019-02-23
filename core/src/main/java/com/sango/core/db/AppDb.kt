package com.sango.core.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.sango.core.model.AccessToken

/**
 * Main database description.
 */
@Database(
    entities = [AccessToken::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {

    companion object {
        private const val DB_NAME = "LUNCHI_DB_NAME"

        fun instance(context: Context): AppDb {
            return Room.databaseBuilder(
                context,
                AppDb::class.java, DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun accessTokenDao(): AccessTokenDao
}
