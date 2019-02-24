package com.sango.core.model

import android.arch.persistence.room.Entity
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.NonNull
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["id"])
data class Restaurant(
    @NonNull
    @field:SerializedName("id")
    val id: Long,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("address")
    val address: String,
    @field:SerializedName("deliveryAreas")
    val deliveryAreas: String,
    @field:SerializedName("coordinates")
    val coordinates: String,
    @field:SerializedName("rating")
    val rating: String,
    @field:SerializedName("logo")
    val logo: String
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readLong(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeString(name)
        writeString(description)
        writeString(address)
        writeString(deliveryAreas)
        writeString(coordinates)
        writeString(rating)
        writeString(logo)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Restaurant> = object : Parcelable.Creator<Restaurant> {
            override fun createFromParcel(source: Parcel): Restaurant = Restaurant(source)
            override fun newArray(size: Int): Array<Restaurant?> = arrayOfNulls(size)
        }
    }
}
