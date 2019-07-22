package com.demo.repository.local

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "userdata")
data class DeliveryData(

    @PrimaryKey val id: Int,

    @SerializedName("description")
    @ColumnInfo(name = "description") val description: String?,

    @SerializedName("imageUrl")
    @ColumnInfo(name = "image_url") val imageUrl: String?,

    @SerializedName("location")
    @ColumnInfo(name = "location")
    var location: LocationData,

// to be consistent w/ changing backend order, we need to keep a data like this
    var indexInResponse: Int = -1
) : Parcelable {
    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DeliveryData> = object : Parcelable.Creator<DeliveryData> {
            override fun createFromParcel(source: Parcel): DeliveryData = DeliveryData(source)
            override fun newArray(size: Int): Array<DeliveryData?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString(),
        source.readString(),
        source.readParcelable<LocationData>(LocationData::class.java.classLoader),
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(description)
        writeString(imageUrl)
        writeParcelable(location, 0)
        writeInt(indexInResponse)
    }

}