package com.example.liebmovies.network.repositories

import com.example.liebmovies.interfaces.ApiInterface
import com.example.liebmovies.interfaces.repository.GetMoviesRepositoryOperations
import com.example.liebmovies.network.models.MoviesResponse
import com.example.liebmovies.network.utils.SafeApiRequest
import javax.inject.Inject

class GetMoviesRepository @Inject constructor(private val service: ApiInterface) :
    GetMoviesRepositoryOperations , SafeApiRequest() {
    override suspend fun getMoviesList(searchToken: String, apiKey: String): MoviesResponse {
        return safeApiRequest { service.getMovies(searchToken, apiKey) }
    }
}


