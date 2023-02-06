package com.example.liebmovies.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.liebmovies.dependencyinjection.MainDispatcher
import com.example.liebmovies.network.usecases.GetMovieDetailsUseCase
import com.example.liebmovies.network.usecases.GetMoviesUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class MoviesViewModelFactory @Inject constructor (@MainDispatcher val dispatcher: CoroutineDispatcher, val getMoviesUseCase: GetMoviesUseCase,
                                                  val getMovieDetailsUseCase: GetMovieDetailsUseCase
): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MoviesViewModel(dispatcher, getMoviesUseCase, getMovieDetailsUseCase) as T
        }
}