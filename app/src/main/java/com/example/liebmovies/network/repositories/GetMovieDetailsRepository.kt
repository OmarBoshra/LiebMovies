package com.example.liebmovies.network.repositories

import com.example.liebmovies.interfaces.ApiInterface
import com.example.liebmovies.interfaces.repository.GetMovieDetailsRepositoryOperations
import com.example.liebmovies.models.MovieDetailsResponse
import com.example.liebmovies.network.utils.SafeApiRequest
import javax.inject.Inject

class GetMovieDetailsRepository @Inject constructor(private val service: ApiInterface) :
    GetMovieDetailsRepositoryOperations , SafeApiRequest() {

    override suspend fun getspecificMovieDetails(imbdId: String, apiKey: String): MovieDetailsResponse {
        return safeApiRequest { service.getspecificMovieDetails(imbdId, apiKey) }
    }
}


