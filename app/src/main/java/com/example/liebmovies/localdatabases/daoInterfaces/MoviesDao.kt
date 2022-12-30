package com.example.liebmovies.localdatabases.daoInterfaces

import androidx.room.*
import com.example.liebmovies.localdatabases.models.Movies

@Dao
interface MoviesDao {
    @Query("SELECT EXISTS ( SELECT  1  FROM " + Movies.TABLE_MOVIES + " WHERE " + Movies.IMDBID + " = :imbdId)")
    suspend fun ifExists(imbdId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(movies: Movies)

    @Update
    suspend fun update(movies: Movies)
}