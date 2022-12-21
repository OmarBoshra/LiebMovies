package com.example.liebmovies.interfaces

import com.example.liebmovies.models.MovieDetailsResponse
import com.example.liebmovies.models.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/** # ApiInterface
 *  @param s is the search token added by the user ,
 *  @param i is the imdbid  ,
 *  @param apikey generated by omdbapi  ,
 */
interface ApiInterface {

    @GET("?r=json&y=2022")
    suspend fun getMovies(
        @Query("s") searchToken: String, @Query("apiKey") apiKey: String
    ): Response<MoviesResponse>

    @GET(".")
    suspend fun getspecificMovieDetails(
        @Query("i") imbdId: String, @Query("apiKey") apiKey: String
    ): Response<MovieDetailsResponse>
}
