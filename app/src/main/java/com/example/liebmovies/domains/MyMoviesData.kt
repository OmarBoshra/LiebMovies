package com.example.liebmovies.domains

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *  this class is basically for the recycler view viewholders , the nullable params are optional
 */
@Parcelize
data class MyMoviesData(
    val imdbId: String,
    val title: String?,
    val year: String?,
    val type: String?,
    val posterUrl: String? = null,
    var posterBitmap: Bitmap? = null
) : Parcelable
