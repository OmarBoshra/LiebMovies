package com.example.liebmovies.localdatabases.databases

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.liebmovies.domains.MyMovieDetails
import com.example.liebmovies.domains.MyMoviesData
import java.io.ByteArrayOutputStream

/** # LocalMovies
 *  the local database that saves the movies lists as well as their specific details ,
 *  the lints with regards to changing variable names here I ignored since SQlite has a different schema
 */
open class LocalMoviesSqliHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    companion object {
        const val VERSION = 1
        const val DATABASE_NAME = "local movies"

        const val IMDBID = "imdbid"
        const val POSTERIMAGE = "posterImage"

        const val TABLE_MOVIES = "table_movies"
        const val MOVIESID = "id"
        const val TITLE = "title"
        const val YEAR = "year"
        const val TYPE = "type"

        const val TABLE_MOVIEDETAILS = "moviedetails"
        const val MOVIEID = "id"
        const val RELEASE = "releases"
        const val LANGUAGES = "languages"
        const val RATING = "rating"
        const val GENRE = "genre"
        const val COUNTRY = "country"
        const val PLOT = "plot"
        const val ACTORS = "actors"
        const val BOXOFFICE = "boxoffice"
        const val AWARDS = "awards"

        const val TABLE_MOVIES_FILTERS = "moviefilters"
        const val FILTERKEYWORD = "filterkeyword"

        // region indexs and triggers
        const val Movies_id_idx = "Movies_id_idx"
        const val insert_filtered_movie_trigger = "insert_filtered_movie_trigger"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_MOVIES_TABLE =
            ("CREATE TABLE " + TABLE_MOVIES + "(" + MOVIESID + " INTEGER PRIMARY KEY," + IMDBID + " INTEGER UNIQUE," + FILTERKEYWORD + " TEXT," + TITLE + " TEXT UNIQUE," + YEAR + " TEXT," + TYPE + " TEXT," + POSTERIMAGE + " BLOB" + ")")
        db?.execSQL(CREATE_MOVIES_TABLE)

        val CREATE_MOVIES_FILTERS_TABLE =
            ("CREATE TABLE " + TABLE_MOVIES_FILTERS + "(" + FILTERKEYWORD + " INTEGER UNIQUE," + IMDBID + " INTEGER UNIQUE" + ")")
        db?.execSQL(CREATE_MOVIES_FILTERS_TABLE)

        val CREATE_MOVIEDETAILS_TABLE =
            ("CREATE TABLE " + TABLE_MOVIEDETAILS + "(" + MOVIEID + " INTEGER PRIMARY KEY," + IMDBID + " INTEGER UNIQUE," + RELEASE + " TEXT," + LANGUAGES + " TEXT," + RATING + " TEXT," + GENRE + " TEXT," + COUNTRY + " TEXT," + PLOT + " TEXT," + ACTORS + " TEXT," + BOXOFFICE + " TEXT," + AWARDS + " TEXT" + ")")
        db?.execSQL(CREATE_MOVIEDETAILS_TABLE)

        val create_movies_id_index = " CREATE INDEX $Movies_id_idx ON $TABLE_MOVIES($MOVIESID)"
        db?.execSQL(create_movies_id_index)

        val create_insert_filtered_movie =
            " CREATE TRIGGER " + insert_filtered_movie_trigger + " AFTER INSERT ON " + TABLE_MOVIES + "\n" + "  BEGIN " + "\n" + "   INSERT OR IGNORE INTO " + TABLE_MOVIES_FILTERS + " (" + FILTERKEYWORD + "," + IMDBID + ") values (new." + FILTERKEYWORD + ",new." + IMDBID + "); \n" + "  END;"
        db?.execSQL(create_insert_filtered_movie)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIEDETAILS")
    }

    // region implemented queries

    @SuppressLint("Range") // the range coming as -1 is the warning but since the same variables are used it shouldn't be a problem
    open fun getMovies(): ArrayList<MyMoviesData> {
        val moviesModelList: ArrayList<MyMoviesData> = ArrayList()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_MOVIES", null
        )

        if (cursor.moveToFirst()) {
            do {

                val isPosterNull = cursor.isNull(cursor.getColumnIndex(POSTERIMAGE))
                var posterImage: Bitmap? = null
                if (!isPosterNull) {
                    val blob = cursor.getBlob(cursor.getColumnIndex(POSTERIMAGE))
                    posterImage = BitmapFactory.decodeByteArray(blob, 0, blob.size)
                }
                moviesModelList.add(
                    MyMoviesData(
                        imdbId = cursor.getString(cursor.getColumnIndex(IMDBID)),
                        title = cursor.getString(cursor.getColumnIndex(TITLE)),
                        year = cursor.getString(cursor.getColumnIndex(YEAR)),
                        type = cursor.getString(cursor.getColumnIndex(TYPE)),
                        posterBitmap = posterImage
                    )
                )
            } while (cursor.moveToNext())
            cursor.close()
            close()
        }
        return moviesModelList
    }


    @SuppressLint("Range")
    open fun getMovieDetails(
        Id: String, title: String?, type: String?, posterImage: Bitmap?
    ): MyMovieDetails? {
        var movieDetails: MyMovieDetails? = null
        val cursor = readableDatabase.rawQuery(
            "SELECT " + RELEASE + "," + LANGUAGES + " ," + RATING + " ," + GENRE + " ," + COUNTRY + " ," + PLOT + " ," + ACTORS + " ," + BOXOFFICE + " ," + AWARDS + " FROM " + TABLE_MOVIEDETAILS + " WHERE " + IMDBID + "=?",
            arrayOf(Id)
        )

        if (cursor.moveToFirst()) {
            movieDetails = MyMovieDetails(
                title,
                type,
                posterImage,
                cursor.getString(cursor.getColumnIndex(RELEASE)),
                cursor.getString(cursor.getColumnIndex(LANGUAGES)),
                cursor.getString(cursor.getColumnIndex(RATING)),
                cursor.getString(cursor.getColumnIndex(GENRE)),
                cursor.getString(cursor.getColumnIndex(COUNTRY)),
                cursor.getString(cursor.getColumnIndex(PLOT)),
                cursor.getString(cursor.getColumnIndex(ACTORS)),
                cursor.getString(cursor.getColumnIndex(BOXOFFICE)),
                cursor.getString(cursor.getColumnIndex(AWARDS))
            )
        }
        cursor.close()
        close()

        return movieDetails
    }

    /**
     *  the movies are inserted one by one when
     *  their viewholders are binded to recycler view because of the time Glide takes
     *  to retrive their urls ,
     */
    open fun insertMovie(movie: MyMoviesData, filterKeyword: String) {
        val db = writableDatabase
        val values = ContentValues()

        values.put(IMDBID, movie.imdbId)
        values.put(FILTERKEYWORD, filterKeyword)
        values.put(TITLE, movie.title)
        values.put(YEAR, movie.year)
        values.put(TYPE, movie.type)

        movie.posterBitmap?.let {
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            values.put(POSTERIMAGE, stream.toByteArray())
        }

        val isMovieAdded = db.rawQuery(
            "SELECT EXISTS ( SELECT  1  FROM $TABLE_MOVIES WHERE $IMDBID=?)", arrayOf(movie.imdbId)
        )
        isMovieAdded.moveToFirst()
        if (isMovieAdded.getInt(0) > 0) {
            db.update(TABLE_MOVIES, values, "$IMDBID=?", arrayOf(IMDBID))
            values.clear()
            isMovieAdded.close()
            close()
        } else {
            db.insert(TABLE_MOVIES, null, values)
            values.clear()
            isMovieAdded.close()
            close()
        }
    }

    open fun insertMovieDetails(
        imbdId: String, movieDetails: MyMovieDetails
    ) {
        val db = writableDatabase

        val isMovieDetailsAdded = db.rawQuery(
            "SELECT EXISTS ( SELECT  1  FROM $TABLE_MOVIEDETAILS WHERE $IMDBID=?)", arrayOf(imbdId)
        )

        val values = ContentValues()
        values.put(IMDBID, imbdId)
        values.put(RELEASE, movieDetails.release)
        values.put(LANGUAGES, movieDetails.language)
        values.put(RATING, movieDetails.rating)
        values.put(GENRE, movieDetails.genre)
        values.put(COUNTRY, movieDetails.country)
        values.put(PLOT, movieDetails.plot)
        values.put(ACTORS, movieDetails.actors)
        values.put(BOXOFFICE, movieDetails.boxOffice)
        values.put(AWARDS, movieDetails.awards)

        isMovieDetailsAdded.moveToFirst()
        if (isMovieDetailsAdded.getInt(0) > 0) {
            db.update(TABLE_MOVIEDETAILS, values, "$IMDBID=?", arrayOf(IMDBID))
            values.clear()
            isMovieDetailsAdded.close()
            close()
        } else {
            db.insert(TABLE_MOVIEDETAILS, null, values)
            values.clear()
            isMovieDetailsAdded.close()
            close()
        }
    }
}


