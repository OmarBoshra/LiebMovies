package com.example.liebmovies.network.usecases

import com.example.liebmovies.network.models.MoviesResponse
import com.example.liebmovies.network.repositories.GetMoviesRepository
import com.example.liebmovies.network.utils.MoviesResult
import javax.inject.Inject

// this is in order to prevent usage if application context in viewModel,
// and instead inject the useCases in the view model for every user action

class GetMoviesUseCase @Inject constructor(private val repository: GetMoviesRepository) {
    suspend operator fun invoke(searchToken: String, apiKey: String): MoviesResult<MoviesResponse> {
        return try {
            val movieResponse = repository.getMoviesList(searchToken, apiKey)
            MoviesResult.Success(movieResponse)
        } catch (e: Exception) {
            MoviesResult.Error(e.message)
        }
    }
}