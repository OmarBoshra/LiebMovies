package com.example.liebmovies.interfaces

import com.example.liebmovies.dependencyinjection.RetroModule
import com.example.liebmovies.viewmodels.MoviesViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RetroModule::class])
interface RetroComponent {

    fun inject(moviesViewModel: MoviesViewModel)
}