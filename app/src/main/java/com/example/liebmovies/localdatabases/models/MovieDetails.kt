package com.example.liebmovies.localdatabases.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = MovieDetails.TABLE_MOVIEDETAILS, indices = [Index(value = [MovieDetails.IMDBID], unique = true)])
class MovieDetails(
    @ColumnInfo(name = MOVIEID)
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = IMDBID) val imbdId: String?,
    @ColumnInfo(name = RELEASE) val release: String?,
    @ColumnInfo(name = LANGUAGE) val language: String?,
    @ColumnInfo(name = RATING) val rating: String?,
    @ColumnInfo(name = GENRE) val genre: String?,
    @ColumnInfo(name = COUNTRY) val country: String?,
    @ColumnInfo(name = PLOT) val plot: String?,
    @ColumnInfo(name = ACTORS) val actors: String?,
    @ColumnInfo(name = BOXOFFICE) val boxOffice: String?,
    @ColumnInfo(name = AWARDS) val awards: String?,
) {
    companion object {
        const val TABLE_MOVIEDETAILS = "moviedetails"
        const val MOVIEID = "id"
        const val IMDBID = "imdbid"
        const val RELEASE = "release"
        const val LANGUAGE = "language"
        const val RATING = "rating"
        const val GENRE = "genre"
        const val COUNTRY = "country"
        const val PLOT = "plot"
        const val ACTORS = "actors"
        const val BOXOFFICE = "boxoffice"
        const val AWARDS = "awards"
    }
}