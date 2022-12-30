package com.example.liebmovies.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailsResponse(
    @SerialName("Released") val release: String? = null,
    @SerialName("Language") val language: String? = null,
    @SerialName("imdbRating") val rating: String? = null,
    @SerialName("Genre") val genre: String? = null,
    @SerialName("Country") val country: String? = null,
    @SerialName("Plot") val plot: String? = null,
    @SerialName("Actors") val actors: String? = null,
    @SerialName("BoxOffice") val boxoffice: String? = null,
    @SerialName("Awards") val awards: String? = null
)
