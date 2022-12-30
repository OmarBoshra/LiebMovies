package com.example.liebmovies.localdatabases.databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.liebmovies.localdatabases.daoInterfaces.MovieDetailsDao
import com.example.liebmovies.localdatabases.daoInterfaces.MoviesAndMoviesFiltersDao
import com.example.liebmovies.localdatabases.daoInterfaces.MoviesDao
import com.example.liebmovies.localdatabases.models.MovieDetails
import com.example.liebmovies.localdatabases.models.Movies
import com.example.liebmovies.localdatabases.models.MoviesFilters
import com.example.liebmovies.localdatabases.utils.Converters

@Database(entities = [Movies::class, MovieDetails::class, MoviesFilters::class], version = 1, exportSchema = false)
abstract class LocalMoviesDb : RoomDatabase() {
    abstract fun moviesDao(): MoviesDao
    abstract fun movieDetailsDao(): MovieDetailsDao

    abstract fun moviesAndMoviesFiltersDao(): MoviesAndMoviesFiltersDao

    companion object {
        @Volatile
        private var instance: LocalMoviesDb? = null
        private var initialDataBaseOperationsCalled = true

        private val callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                if (initialDataBaseOperationsCalled) {
                    initialDataBaseOperationsCalled = false
                    db.execSQL(MoviesAndMoviesFiltersDao.INSERT_FILTERED_MOVIE_TRIGGER)
                } else {
                    return
                }
            }
        }

        fun getInstance(context: Context): LocalMoviesDb {

            val tempInstance = instance
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalMoviesDb::class.java,
                    "movie_database"
                ).addCallback(callback).build()
                instance = newInstance
                return newInstance
            }

        }
    }
    class Migration1to2 : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add a new column to the users table
        }
    }
}
