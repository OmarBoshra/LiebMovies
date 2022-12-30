package com.example.liebmovies.localdatabases.daoInterfaces

import androidx.room.*
import com.example.liebmovies.localdatabases.models.Movies
import com.example.liebmovies.localdatabases.models.MoviesAndMoviesFilters
import com.example.liebmovies.localdatabases.models.MoviesFilters

@Dao
interface MoviesAndMoviesFiltersDao {
    /**
     * getMovies
     * the transaction annotation is there since this query involves multiple queries
     * @param filterKeyword
     * @return MoviesAndMoviesFilters which has the joining of the one to many relationship between the movies and their filter tokens
     */
    @Transaction
    @Query("SELECT " + MoviesFilters.FILTERKEYWORD + " FROM " + MoviesFilters.TABLE_MOVIES_FILTERS + " WHERE " + MoviesFilters.FILTERKEYWORD + " = :filterKeyword")
    fun getMovies(filterKeyword: String): MoviesAndMoviesFilters

    companion object {
        const val INSERT_FILTERED_MOVIE_TRIGGER =
            " CREATE TRIGGER insert_filtered_movie_trigger AFTER INSERT ON " + Movies.TABLE_MOVIES +
                    "\n" + "  BEGIN " +
                    "\n" + "INSERT OR REPLACE INTO " + MoviesFilters.TABLE_MOVIES_FILTERS + " (" + MoviesFilters.MOVIEID + "," + MoviesFilters.FILTERKEYWORD + ") values (new." + Movies.MOVIEID + ",new." + Movies.FILTERKEYWORD + "); \n" + "  END;"
    }
}