package com.demo.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LocationData(
    @SerializedName("lat") var lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("address") val address: String
) : Parcelable {
    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<LocationData> = object : Parcelable.Creator<LocationData> {
            override fun createFromParcel(source: Parcel): LocationData =
                LocationData(source)
            override fun newArray(size: Int): Array<LocationData?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
        source.readDouble(),
        source.readDouble(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeDouble(lat)
        writeDouble(lng)
        writeString(address)
    }
}