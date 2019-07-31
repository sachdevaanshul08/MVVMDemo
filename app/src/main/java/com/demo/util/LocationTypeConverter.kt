package com.demo.util

import androidx.room.TypeConverter
import com.demo.repository.model.LocationData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocationTypeConverter {

    /**
     * Convert a a list of Locations data to a Json
     */
    @TypeConverter
    fun fromLocation(stat: LocationData): String {
        return Gson().toJson(stat)
    }

    /**
     * Convert a json to a list of Locations data
     */
    @TypeConverter
    fun toLocation(jsonLocation: String): LocationData {
        val notesType = object : TypeToken<LocationData>() {}.type
        return Gson().fromJson<LocationData>(jsonLocation, notesType)
    }
}