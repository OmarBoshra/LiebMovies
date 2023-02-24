package com.example.liebmovies.localdatabases.daoInterfaces

import androidx.room.*
import com.example.liebmovies.localdatabases.models.MoviesFilters

@Dao
interface MoviesFiltersDao {
    @Query("SELECT " + MoviesFilters.FILTERKEYWORD + " FROM " + MoviesFilters.TABLE_MOVIES_FILTERS + " GROUP BY " + MoviesFilters.FILTERKEYWORD)
    suspend fun getSearchTokens(): List<String>

}