package com.sango.core.util

import android.os.SystemClock
import android.support.v4.util.ArrayMap
import java.util.concurrent.TimeUnit

/**
 * Utility class that decides whether we should fetch some data or not.
 */
class RateLimiter<in KEY>(timeout: Int, timeUnit: TimeUnit) {
    private val timestamps = ArrayMap<KEY, Long>()
    private val timeout = timeUnit.toMillis(timeout.toLong())

    /**
     * Returns a boolean that indicates that a fetch should be performed
     * for the corresponding KEY.
     * @param KEY The key to associated to the rateLimiter
     * @param timeInMillis optional, used mainly for unit-testing (value is injected when necessary)
     * @return boolean value
     */
    @Synchronized
    fun shouldFetch(key: KEY, timeInMillis: Long = now()): Boolean {
        val lastFetched = timestamps[key]
        val now = timeInMillis
        return if ((lastFetched == null) || (now - lastFetched > timeout)) {
            timestamps[key] = now
            true
        } else {
            false
        }
    }

    private fun now() = SystemClock.uptimeMillis()

    @Synchronized
    fun reset(key: KEY) {
        timestamps.remove(key)
    }
}
