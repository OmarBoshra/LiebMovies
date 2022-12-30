package com.example.liebmovies.localdatabases.models

import android.graphics.Bitmap
import androidx.room.*
import com.example.liebmovies.localdatabases.utils.Converters

@Entity(
    tableName = Movies.TABLE_MOVIES, indices = [Index(value = [Movies.TITLE], unique = true), Index(
        value = [Movies.IMDBID], unique = true
    )]
)
@TypeConverters(Converters::class)
data class Movies(
    @ColumnInfo(name = MOVIEID) @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = IMDBID) val imbdId: String,
    @ColumnInfo(name = TITLE) val title: String? = null,
    @ColumnInfo(name = TYPE) val type: String? = null,
    @ColumnInfo(name = YEAR) val year: String? = null,
    @ColumnInfo(
        name = POSTERIMAGE, typeAffinity = ColumnInfo.BLOB
    ) val posterImage: Bitmap? = null,
    @ColumnInfo(name = FILTERKEYWORD) var filterKeyWord: String? = null
) {
    companion object {
        const val TABLE_MOVIES = "movies"
        const val MOVIEID = "movieId"
        const val IMDBID = "imdbid"
        const val TITLE = "title"
        const val YEAR = "year"
        const val TYPE = "type"
        const val FILTERKEYWORD = "moviefilterkeyword"
        const val POSTERIMAGE = "posterImage"
    }
}
