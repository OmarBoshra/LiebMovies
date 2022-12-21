package com.example.liebmovies.commons

import android.graphics.Bitmap

/**
 *  to transmit the clicked data from the viewholders all the way to the viewmodel in order
 *  to use them to get responses from the local database,
 */
object ClickedMovieParams {
    var imdbId: String? = null
    var title: String? = null
    var posterImage: Bitmap? = null
    var type: String? = null
}