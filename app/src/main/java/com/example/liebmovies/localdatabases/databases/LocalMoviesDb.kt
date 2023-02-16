package com.example.liebmovies.localdatabases.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.liebmovies.localdatabases.daoInterfaces.MovieDetailsDao
import com.example.liebmovies.localdatabases.daoInterfaces.MoviesAndMoviesFiltersDao
import com.example.liebmovies.localdatabases.daoInterfaces.MoviesDao
import com.example.liebmovies.localdatabases.daoInterfaces.MoviesFiltersDao
import com.example.liebmovies.localdatabases.models.MovieDetails
import com.example.liebmovies.localdatabases.models.Movies
import com.example.liebmovies.localdatabases.models.MoviesFilters

@Database(
    entities = [Movies::class, MovieDetails::class, MoviesFilters::class],
    version = 1,
    exportSchema = false
)
abstract class LocalMoviesDb : RoomDatabase() {
    abstract fun moviesDao(): MoviesDao
    abstract fun movieDetailsDao(): MovieDetailsDao

    abstract fun moviesAndMoviesFiltersDao(): MoviesAndMoviesFiltersDao
    abstract fun moviesFiltersDao(): MoviesFiltersDao

    companion object {
        @Volatile
        private var instance: LocalMoviesDb? = null
        private var initialDataBaseOperationsCalled = true

        /**
         * This Callback is used to instantiate the trigger function
         * @since Room doesnt yet support tiggers
         */

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
