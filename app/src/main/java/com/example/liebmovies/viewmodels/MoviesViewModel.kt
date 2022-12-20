package com.example.liebmovies.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.liebmovies.MoviesApplication
import com.example.liebmovies.R
import com.example.liebmovies.interfaces.ApiInterface
import com.example.liebmovies.models.*
import com.example.liebmovies.sqlitedatabases.LocalMovies
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/** # MoviesViewModel
 *  Utilizes the retrofit service to manage user requests ,
 */
class MoviesViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var mService: ApiInterface

    // params that notify the MoviesListFragment about the results of the user's requests
    var liveMoviesDataList = MutableLiveData<ArrayList<MoviesData>>()
    var liveMoviesDataListFailure = MutableLiveData<String>()

    var liveMoviesDataListLocal = MutableLiveData<ArrayList<MoviesData>>()
    var liveMoviesDataListLocalFailure = MutableLiveData<String>()

    var liveMovieDetails = MutableLiveData<MovieDetails>()
    var liveMovieDetailsFailure = MutableLiveData<String>()

    var liveMovieDetailsLocal = MutableLiveData<MovieDetails>()
    var liveMovieDetailsLocalFailure = MutableLiveData<String>()

    val errorMessage: String

    init {
        // initializing the application class
        (application as MoviesApplication).getRetroComponent().inject(this)
        errorMessage = (application.getString(R.string.message_empty_results))
    }

    // region get requests
    fun getMovies(searchToken: String, apiKey: String) {
        viewModelScope.launch {
            val moviesDataResponse = mService.getMovies(searchToken, apiKey)
            moviesDataResponse.enqueue(object : Callback<MoviesResponse> {
                override fun onResponse(
                    call: Call<MoviesResponse?>, response: Response<MoviesResponse?>
                ) {

                    val responseBody = response.body()

                    if (responseBody != null) {

                        val moviesListData = ArrayList<MoviesData>()


                        responseBody.search.forEach { search ->

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
                    } else {
                        liveMoviesDataListFailure.value = errorMessage
                    }
                }

                override fun onFailure(call: Call<MoviesResponse>, error: Throwable) {
                    val errorMessage = "err $error happened"
                    liveMoviesDataListFailure.value = errorMessage
                }
            })
        }
    }

    fun getMovieDetails(
        imdbId: String?, posterBitmap: Bitmap?, title: String?, type: String?, apiKey: String
    ) {
        val movieDataResponse = imdbId?.let { mService.getspecificMovieDetails(it, apiKey) }
        movieDataResponse?.enqueue(object : Callback<MovieDetailsResponse> {
            override fun onResponse(
                call: Call<MovieDetailsResponse?>, response: Response<MovieDetailsResponse?>
            ) {

                val responseBody = response.body()

                responseBody?.let {

                    val remoteMovieDetails = MovieDetails(
                        title,
                        type,
                        posterBitmap,
                        responseBody.release,
                        responseBody.actors,
                        responseBody.awards,
                        responseBody.country,
                        responseBody.language,
                        responseBody.plot,
                        responseBody.boxoffice,
                        responseBody.rating,
                        responseBody.genre,
                    )

                    liveMovieDetails.value = remoteMovieDetails

                } ?: run {
                    liveMovieDetailsFailure.value = errorMessage
                }
            }

            override fun onFailure(call: Call<MovieDetailsResponse>, error: Throwable) {
                val errorMessage = "err $error happened"
                liveMovieDetailsFailure.value = errorMessage
            }
        })
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
                liveMoviesDataListLocalFailure.value = errorMessage
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
            liveMovieDetailsLocalFailure.value = errorMessage
        }
    }

    // region end
}