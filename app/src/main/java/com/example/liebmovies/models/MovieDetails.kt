package com.example.liebmovies.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class MovieDetails(
    var title: String?,
    var type: String?,
    var posterBitmap: Bitmap? = null,
    val release: String?,
    val language: String?,
    val rating: String?,
    val genre: String?,
    val country: String?,
    val plot: String?,
    val actors: String?,
    val boxOffice: String?,
    val awards: String?,
) : Parcelable
