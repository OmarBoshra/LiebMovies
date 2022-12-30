package com.example.liebmovies.localdatabases.daoInterfaces

import androidx.room.*
import com.example.liebmovies.localdatabases.models.MovieDetails
import com.example.liebmovies.localdatabases.models.Movies

@Dao
interface MovieDetailsDao {
    @Query("SELECT EXISTS ( SELECT  1  FROM " + MovieDetails.TABLE_MOVIEDETAILS + " WHERE " + MovieDetails.IMDBID +" = :imbdId)")
    suspend fun ifExists(imbdId : String?): Boolean

    @Query("SELECT * FROM " + MovieDetails.TABLE_MOVIEDETAILS+ " WHERE " + MovieDetails.IMDBID + "= :imbdId")
    suspend fun getMovieDetails(imbdId : String): MovieDetails

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(movieDetails: MovieDetails)

    @Update
    suspend fun update(movieDetails: MovieDetails)
}