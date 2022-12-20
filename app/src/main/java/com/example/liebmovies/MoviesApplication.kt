package com.example.liebmovies

import android.app.Application
import com.example.liebmovies.dependencyinjection.RetroModule
import com.example.liebmovies.interfaces.DaggerRetroComponent
import com.example.liebmovies.interfaces.RetroComponent

class MoviesApplication : Application() {

    private lateinit var retroComponent: RetroComponent
    override fun onCreate() {
        super.onCreate()

        /** creating the main retrofit component needed in order to use retrofit via view model ,
         * injecting it in the acyclic graph
         */
        retroComponent = DaggerRetroComponent.builder().retroModule(RetroModule()).build()

    }

    /** the view model calls this method in order to inject itself in the acyclic graph
     */
    fun getRetroComponent(): RetroComponent {
        return retroComponent
    }

}