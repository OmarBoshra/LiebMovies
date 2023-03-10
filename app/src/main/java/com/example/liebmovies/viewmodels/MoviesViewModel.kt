package com.example.liebmovies.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liebmovies.commons.ClickedMovieParams
import com.example.liebmovies.domains.MyMovieDetails
import com.example.liebmovies.domains.MyMoviesData
import com.example.liebmovies.localdatabases.daoInterfaces.MovieDetailsDao
import com.example.liebmovies.localdatabases.daoInterfaces.MoviesDao
import com.example.liebmovies.localdatabases.databases.LocalMoviesDb
import com.example.liebmovies.localdatabases.models.MovieDetails
import com.example.liebmovies.localdatabases.models.Movies
import com.example.liebmovies.network.models.MovieDetailsResponse
import com.example.liebmovies.network.models.MoviesResponse
import com.example.liebmovies.network.usecases.GetMovieDetailsUseCase
import com.example.liebmovies.network.usecases.GetMoviesUseCase
import com.example.liebmovies.network.utils.MoviesResult
import com.example.liebmovies.viewmodels.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** # MoviesViewModel
 *  Utilizes the retrofit service to manage user requests ,
 */
class MoviesViewModel(
    val dispatcher: CoroutineDispatcher,
    val getMoviesUseCase: GetMoviesUseCase,
    val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {

    // params that notify the MoviesListFragment about the results of the user's requests
    internal var liveMyMoviesDataList = SingleLiveEvent<ArrayList<MyMoviesData>>()
    internal var liveMoviesDataListFailure = SingleLiveEvent<String?>()

    internal var liveMyMoviesDataListLocal = SingleLiveEvent<ArrayList<MyMoviesData>>()
    internal var liveMoviesDataListLocalFailure = SingleLiveEvent<String>()

    internal var liveMovieDetails = SingleLiveEvent<MyMovieDetails>()
    internal var liveMovieDetailsFailure = SingleLiveEvent<String?>()

    internal var liveMovieDetailsLocal = SingleLiveEvent<MyMovieDetails>()
    internal var liveMovieDetailsLocalFailure = SingleLiveEvent<String>()

    internal var liveSearchText: LiveData<CharSequence>
    internal var MutableLiveSearchText = SingleLiveEvent<CharSequence>()

    internal var LiveSearchTokens = SingleLiveEvent<ArrayList<String>>()

    init {
        liveSearchText = MutableLiveSearchText
    }

    // region get requests
    fun getMovies(searchToken: String, apiKey: String) {
        viewModelScope.launch {
            val moviesResult = getMoviesUseCase.invoke(searchToken, apiKey)

            getMoviesResult(moviesResult)
        }
    }

    private fun getMoviesResult(result: MoviesResult<MoviesResponse>) {
        when (result) {
            is MoviesResult.Success -> {
                val moviesListData = ArrayList<MyMoviesData>()
                result.data.search.forEach { search ->

                    moviesListData.add(
                        MyMoviesData(
                            search.imdbId,
                            search.title,
                            search.year,
                            search.type,
                            posterUrl = search.posterUrl
                        )
                    )
                }
                liveMyMoviesDataList.value = moviesListData
            }
            is MoviesResult.Error -> {
                liveMoviesDataListFailure.value = result.errorMessage
            }
        }
    }

    fun getMovieDetails(
        imdbId: String, posterBitmap: Bitmap?, title: String?, type: String?, apiKey: String
    ) {
        viewModelScope.launch {
            val movieDataResponse = getMovieDetailsUseCase.invoke(imdbId, apiKey)
            getMovieDetailsResult(title, type, posterBitmap, movieDataResponse)
        }
    }

    private fun getMovieDetailsResult(
        title: String?,
        type: String?,
        posterBitmap: Bitmap?,
        result: MoviesResult<MovieDetailsResponse>
    ) {
        when (result) {
            is MoviesResult.Success -> {
                val remoteMovieDetails = MyMovieDetails(
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

    // end region

    // region local db useCases

    // region local inserts , which happen in back ground after a successful network response
    fun insertingMoviesRequest(
        moviesListData: MyMoviesData?,
        searchToken: String,
        context: Context
    ) {
        val db = LocalMoviesDb.getInstance(context)
        val moviesDao = db.moviesDao()

        moviesListData?.let { movie ->

            val moviesModel = Movies(
                null,
                imbdId = movie.imdbId,
                title = movie.title,
                type = movie.type,
                year = movie.year,
                posterImage = movie.posterBitmap

            )
            moviesModel.filterKeyWord = searchToken
            viewModelScope.launch {
                saveMoviesLocally(moviesDao, movie, moviesModel)
            }
        }
    }

    private suspend fun saveMoviesLocally(
        moviesDao: MoviesDao,
        movie: MyMoviesData,
        moviesModel: Movies
    ) {
        if (moviesDao.ifExists(movie.imdbId)) {
            moviesDao.update(moviesModel)
        } else {
            moviesDao.insertOrReplace(moviesModel)
        }
    }

    fun insertingMoviesDetails(
        imdbId: String?, remoteMovieDetails: MyMovieDetails, context: Context
    ) {
        val db = LocalMoviesDb.getInstance(context)
        val movieDetailsDao = db.movieDetailsDao()
        val moviesModel = MovieDetails(
            null,
            imbdId = imdbId,
            remoteMovieDetails.release,
            remoteMovieDetails.language,
            remoteMovieDetails.rating,
            remoteMovieDetails.genre,
            remoteMovieDetails.country,
            remoteMovieDetails.plot,
            remoteMovieDetails.actors,
            remoteMovieDetails.boxOffice,
            remoteMovieDetails.awards
        )


        viewModelScope.launch {
            saveMovieDetailsLocally(movieDetailsDao, imdbId, moviesModel)
        }

    }

    private suspend fun saveMovieDetailsLocally(
        movieDetailsDao: MovieDetailsDao,
        imdbId: String?,
        moviesModel: MovieDetails
    ) {
        if (movieDetailsDao.ifExists(imdbId)) {
            movieDetailsDao.update(moviesModel)
        } else {
            movieDetailsDao.insertOrReplace(moviesModel)
        }
    }

    // region end

    // region local gets

    fun getMoviesListFromLocalStorage(context: Context, searchToken: String) {
        val myMoviesDataList = ArrayList<MyMoviesData>()

        viewModelScope.launch(IO) {
            val db = LocalMoviesDb.getInstance(context)
            val moviesAndFilters = db.moviesAndMoviesFiltersDao()
            // get the local list of movies
            delay(550) // so the user can see new dialog message
            val filteredMovies = moviesAndFilters.getMovies(searchToken)

            filteredMovies?.movies?.let {
                it.forEach { movie ->
                    myMoviesDataList.add(
                        MyMoviesData(
                            movie.imbdId,
                            movie.title,
                            movie.year,
                            movie.type,
                            posterBitmap = movie.posterImage
                        )
                    )
                }
            }
            // Update the UI or communicate the results of the task to the user
            withContext(dispatcher) {
                // if movies locally exists get them
                if (myMoviesDataList.isNotEmpty()) {
                    liveMyMoviesDataListLocal.value = myMoviesDataList
                } else {
                    liveMoviesDataListLocalFailure.value = "errorMessage"
                }
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


        var localMovieDetails: MovieDetails?
        // get the local list of movies
        viewModelScope.launch(IO) {
            val db = LocalMoviesDb.getInstance(context)
            val movieDetails = db.movieDetailsDao()
            delay(550) // so the user can see new dialog message
            localMovieDetails = imdbId?.let { movieDetails.getMovieDetails(it) }

            // if movie locally exists get it then send them to the MovieDetailsFragment
            withContext(dispatcher) {
                localMovieDetails?.let { it ->
                    val myMovieDetails = MyMovieDetails(
                        title,
                        type,
                        posterImage,
                        it.release,
                        it.language,
                        it.rating,
                        it.genre,
                        it.country,
                        it.plot,
                        it.actors,
                        it.boxOffice,
                        it.awards
                    )
                    liveMovieDetailsLocal.value = myMovieDetails
                } ?: run {
                    liveMovieDetailsLocalFailure.value = "errorMessage"
                }
            }
        }

    }

    /**
     * Get search tokens
     * the users search tokens are automatically saved in the local db
     */
    fun getSavedSearchTokens(context: Context) {
        // get the local list of searchTokens
        var myMoviesSearchTokens: ArrayList<String>

        viewModelScope.launch(IO) {
            val db = LocalMoviesDb.getInstance(context)
            val moviesFilters = db.moviesFiltersDao()
            myMoviesSearchTokens = moviesFilters.getSearchTokens() as ArrayList<String>

            withContext(dispatcher) {
                if (myMoviesSearchTokens.isNotEmpty()) {
                    LiveSearchTokens.value = myMoviesSearchTokens
                }
            }

        }
    }

    // region end

    var searchTextWatcher = object : TextWatcher {
        // only in onTextChanged am I filtering the recyclerViewList
        override fun afterTextChanged(s: Editable) {
            // no implementation
        }

        override fun beforeTextChanged(
            s: CharSequence, start: Int, count: Int, after: Int
        ) {// no implementation
        }

        override fun onTextChanged(
            s: CharSequence, start: Int, before: Int, count: Int
        ) {
            MutableLiveSearchText.value = s
        }
    }

}