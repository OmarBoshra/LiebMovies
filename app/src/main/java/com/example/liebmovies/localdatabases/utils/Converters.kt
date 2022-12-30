package com.example.liebmovies.localdatabases.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? {
        return byteArray?.size?.let { BitmapFactory.decodeByteArray(byteArray, 0, it) }
    }

}