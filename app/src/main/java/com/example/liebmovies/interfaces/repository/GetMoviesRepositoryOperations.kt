package com.example.liebmovies.interfaces.repository

import com.example.liebmovies.network.models.MoviesResponse

interface GetMoviesRepositoryOperations {

    suspend fun getMoviesList(searchToken: String, apiKey: String): MoviesResponse
}