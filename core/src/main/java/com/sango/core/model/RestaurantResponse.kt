package com.sango.core.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RestaurantResponse(
    @SerializedName("total")
    val total: Int,
    @SerializedName("max")
    val max: Int,
    @SerializedName("count")
    val count: Int,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("data")
    val restaurants: List<Restaurant>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.createTypedArrayList(Restaurant.CREATOR)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(total)
        parcel.writeInt(max)
        parcel.writeInt(count)
        parcel.writeInt(offset)
        parcel.writeTypedList(restaurants)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<RestaurantResponse> {
        override fun createFromParcel(parcel: Parcel): RestaurantResponse = RestaurantResponse(parcel)

        override fun newArray(size: Int): Array<RestaurantResponse?> = arrayOfNulls(size)
    }
}