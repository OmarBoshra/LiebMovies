package com.example.liebmovies.interfaces.repository

import com.example.liebmovies.network.models.MovieDetailsResponse

interface GetMovieDetailsRepositoryOperations {
    suspend fun getspecificMovieDetails(imbdId: String, apiKey: String): MovieDetailsResponse
}