package com.example.liebmovies.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.liebmovies.R
import com.example.liebmovies.databinding.ActivityMoviesListBinding
import com.example.liebmovies.dependencyinjection.RetroModule
import com.example.liebmovies.interfaces.DaggerRetroComponent
import com.example.liebmovies.interfaces.RetroComponent

/**
 * Movies activity
 *
 * @constructor Create empty Movies activity
 */

class MoviesActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMoviesListBinding

    val retroComponent: RetroComponent by lazy {
        DaggerRetroComponent.builder().retroModule(RetroModule()).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMoviesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_movies_list)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_movies_list)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}