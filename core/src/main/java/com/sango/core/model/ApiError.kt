package com.sango.core.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Class to handle the error from the network
 * request
 */
data class ApiError(

    @SerializedName("statusCode")
    val statusCode: Int?,
    @SerializedName("error")
    val error: String?,
    @SerializedName("message")
    val message: String?

) : Parcelable {

    constructor() : this(
            0,
            "",
            "")

    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(statusCode)
        parcel.writeString(error)
        parcel.writeString(message)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ApiError> {
        override fun createFromParcel(parcel: Parcel) = ApiError(parcel)

        override fun newArray(size: Int): Array<ApiError?> = arrayOfNulls(size)
    }
}
