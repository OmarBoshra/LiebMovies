package com.example.liebmovies.localdatabases.models

import androidx.room.*


@Entity(
    tableName = MoviesFilters.TABLE_MOVIES_FILTERS,
    indices = [Index(value = [Movies .MOVIEID], unique = true)],
    foreignKeys = [ForeignKey(
        entity = Movies::class,
        parentColumns = arrayOf(Movies.MOVIEID),
        childColumns = arrayOf(MoviesFilters.MOVIEID),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
)
class MoviesFilters(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = MOVIEID) val movieId: Long?,
    @ColumnInfo(name = FILTERKEYWORD) val filterKeyWord: String
) {
    companion object {
        const val TABLE_MOVIES_FILTERS = "moviefilters"
        const val MOVIEID = "movieId"
        const val FILTERKEYWORD = "filterkeyword"
    }
}