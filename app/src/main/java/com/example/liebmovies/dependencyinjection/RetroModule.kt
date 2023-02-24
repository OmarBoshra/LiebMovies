package com.example.liebmovies.dependencyinjection

import com.example.liebmovies.interfaces.ApiInterface
import com.example.liebmovies.network.repositories.GetMoviesRepository
import com.example.liebmovies.network.usecases.GetMoviesUseCase
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class RetroModule {

    private val baseURL = "https://www.omdbapi.com/"

    /**
     * here the retro module is built then served to the view-model , the experimentalSerialization Api
     * json converter is in order to use @Serializable annotation in the class in order to use different
     * parameter names
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    @Provides
    fun getRetrofitInstance(): Retrofit {

        val json = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
            .baseUrl(baseURL).build()

    }

    @Singleton
    @Provides
    fun getApiInterface(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)

    }

    @Singleton
    @Provides
    fun getGetMoviesRepository(service: ApiInterface): GetMoviesRepository {
        return GetMoviesRepository(service = service)
    }

    @Singleton
    @Provides
    fun getMoviesUseCase(getGetMoviesRepository: GetMoviesRepository): GetMoviesUseCase {
        return GetMoviesUseCase(getGetMoviesRepository)
    }
}