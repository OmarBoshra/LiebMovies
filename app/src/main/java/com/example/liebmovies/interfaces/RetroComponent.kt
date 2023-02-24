package com.example.liebmovies.interfaces

import com.example.liebmovies.dependencyinjection.DispatchersModule
import com.example.liebmovies.dependencyinjection.RetroModule
import com.example.liebmovies.fragments.MoviesListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RetroModule::class,
        DispatchersModule::class
    ]
)
interface RetroComponent {

    fun inject(moviesListFragment: MoviesListFragment)
}