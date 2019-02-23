package com.sango.core.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Class to handle the error from the network
 * request
 */
data class ApiError(
    @SerializedName("code")
    val error: String?,
    @SerializedName("messages")
    val message: List<String> = arrayListOf()
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.createStringArrayList()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(error)
        writeStringList(message)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ApiError> = object : Parcelable.Creator<ApiError> {
            override fun createFromParcel(source: Parcel): ApiError = ApiError(source)
            override fun newArray(size: Int): Array<ApiError?> = arrayOfNulls(size)
        }
    }
}
