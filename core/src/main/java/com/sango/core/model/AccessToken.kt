package com.sango.core.model

import android.arch.persistence.room.Entity
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.NonNull
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["accessToken"])
data class AccessToken(
    @NonNull
    @field:SerializedName("access_token")
    val accessToken: String
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(accessToken)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AccessToken> = object : Parcelable.Creator<AccessToken> {
            override fun createFromParcel(source: Parcel): AccessToken = AccessToken(source)
            override fun newArray(size: Int): Array<AccessToken?> = arrayOfNulls(size)
        }
    }
}