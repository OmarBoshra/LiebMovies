package com.example.liebmovies.network.usecases

import com.example.liebmovies.network.models.MovieDetailsResponse
import com.example.liebmovies.network.repositories.GetMovieDetailsRepository
import com.example.liebmovies.network.utils.MoviesResult
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(private val repository: GetMovieDetailsRepository) {
    suspend operator fun invoke(
        imbdId: String,
        apiKey: String
    ): MoviesResult<MovieDetailsResponse> {
        return try {
            val movieResponse = repository.getspecificMovieDetails(imbdId, apiKey)
            MoviesResult.Success(movieResponse)
        } catch (e: Exception) {
            MoviesResult.Error(e.message)
        }
    }
}