package com.sango.core.util

import android.content.res.Resources

fun Int.px(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}