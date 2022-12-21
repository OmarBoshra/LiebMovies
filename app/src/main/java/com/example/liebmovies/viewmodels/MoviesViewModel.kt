package com.example.liebmovies.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liebmovies.commons.ClickedMovieParams
import com.example.liebmovies.models.MovieDetails
import com.example.liebmovies.models.MoviesData
import com.example.liebmovies.network.usecases.GetMovieDetailsUseCase
import com.example.liebmovies.network.usecases.GetMoviesUseCase
import com.example.liebmovies.network.utils.MoviesResult
import com.example.liebmovies.sqlitedatabases.LocalMovies
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/** # MoviesViewModel
 *  Utilizes the retrofit service to manage user requests ,
 */
class MoviesViewModel : ViewModel() {

    @Inject
    lateinit var getMoviesUseCase: GetMoviesUseCase

    @Inject
    lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase

    // params that notify the MoviesListFragment about the results of the user's requests
    var liveMoviesDataList = MutableLiveData<ArrayList<MoviesData>>()
    var liveMoviesDataListFailure = MutableLiveData<String?>()

    var liveMoviesDataListLocal = MutableLiveData<ArrayList<MoviesData>>()
    var liveMoviesDataListLocalFailure = MutableLiveData<String>()

    var liveMovieDetails = MutableLiveData<MovieDetails>()
    var liveMovieDetailsFailure = MutableLiveData<String?>()

    var liveMovieDetailsLocal = MutableLiveData<MovieDetails>()
    var liveMovieDetailsLocalFailure = MutableLiveData<String>()


    // region get requests
    fun getMovies(searchToken: String, apiKey: String) {
        viewModelScope.launch {
            val moviesResult = getMoviesUseCase.invoke(searchToken, apiKey)

            moviesResult.let { result ->
                when (result) {
                    is MoviesResult.Success -> {
                        val moviesListData = ArrayList<MoviesData>()
                        result.data.search.forEach { search ->

                            moviesListData.add(
                                MoviesData(
                                    search.imdbId,
                                    search.title,
                                    search.year,
                                    search.type,
                                    posterUrl = search.posterUrl
                                )
                            )
                        }
                        liveMoviesDataList.value = moviesListData
                    }
                    is MoviesResult.Error -> {
                        liveMoviesDataListFailure.value = result.errorMessage
                    }
                }
            }
        }
    }

    fun getMovieDetails(
        imdbId: String, posterBitmap: Bitmap?, title: String?, type: String?, apiKey: String
    ) {
        viewModelScope.launch {
            val movieDataResponse = getMovieDetailsUseCase.invoke(imdbId, apiKey)
            movieDataResponse.let { result ->
                when (result) {
                    is MoviesResult.Success -> {
                        val remoteMovieDetails = MovieDetails(
                            title,
                            type,
                            posterBitmap,
                            result.data.release,
                            result.data.actors,
                            result.data.awards,
                            result.data.country,
                            result.data.language,
                            result.data.plot,
                            result.data.boxoffice,
                            result.data.rating,
                            result.data.genre,
                        )
                        liveMovieDetails.value = remoteMovieDetails
                    }
                    is MoviesResult.Error -> {
                        liveMovieDetailsFailure.value = result.errorMessage
                    }
                }
            }
        }
    }

    // end region

    // region local db useCases

    // region local inserts , which happen in back ground after a successful network response
    fun insertingMoviesRequest(moviesListData: MoviesData?, searchToken: String, context: Context) {
        val db = LocalMovies(context)
        moviesListData?.let {
            db.insertMovie(moviesListData, searchToken)
        }
    }

    fun insertingMoviesDetails(
        imdbId: String?, remoteMovieDetails: MovieDetails, context: Context
    ) {
        // save or update locally
        val db = LocalMovies(context)
        imdbId?.let { db.insertMovieDetails(it, remoteMovieDetails) }
    }

    // region end

    // region local gets

    fun getMoviesListFromLocalStorage(context: Context) {
        viewModelScope.launch {
            // get the local list of movies
            delay(550) // so the user can see new dialog message
            val db = LocalMovies(context)
            val localMovies: ArrayList<MoviesData> = db.getMovies()
            // if movies locally exists get them
            if (localMovies.isNotEmpty()) {
                liveMoviesDataListLocal.value = localMovies

            } else {
                liveMoviesDataListLocalFailure.value = "errorMessage"
            }
        }
    }

    fun getMovieDetailsFromLocalStorage(
        clickedMovieParameters: ClickedMovieParams, context: Context
    ) {
        val imdbId = clickedMovieParameters.imdbId
        val title = clickedMovieParameters.title
        val type = clickedMovieParameters.type
        val posterImage = clickedMovieParameters.posterImage

        val db = LocalMovies(context)
        val localMovieDetails: MovieDetails? =
            imdbId?.let { db.getMovieDetails(it, title, type, posterImage) }
        // if movie locally exists get it then send them to the MovieDetailsFragment
        localMovieDetails?.let {
            liveMovieDetailsLocal.value = it
        } ?: run {
            liveMovieDetailsLocalFailure.value = "errorMessage"
        }
    }

    // region end
}