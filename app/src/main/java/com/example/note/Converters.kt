package com.example.note

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.util.stream.Collectors

object Converters {
    @TypeConverter
    fun fromString(value: String): ArrayList<String> {
        return ArrayList(value.split(","))
    }

    @TypeConverter
    fun toString(data: ArrayList<String>): String {
        return data.stream().collect(Collectors.joining(","))
    }
}