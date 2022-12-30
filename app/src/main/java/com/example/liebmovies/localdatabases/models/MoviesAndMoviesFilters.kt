package com.example.liebmovies.localdatabases.models

import androidx.room.Relation

class MoviesAndMoviesFilters {
    lateinit var filterkeyword: String
    @Relation(parentColumn = MoviesFilters.FILTERKEYWORD, entityColumn = Movies.FILTERKEYWORD)
    lateinit var movies: List<Movies>
}