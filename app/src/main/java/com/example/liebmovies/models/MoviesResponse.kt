package com.example.liebmovies.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoviesResponse(
    @SerialName("Search") val search: List<Search>,
    @SerialName("totalResults") val totalResults: String
)
// domain model class
@Serializable
data class Search(
    @SerialName("imdbID") val imdbId: String,
    @SerialName("Poster") val posterUrl: String,
    @SerialName("Title") val title: String,
    @SerialName("Type") val type: String,
    @SerialName("Year") val year: String
)
