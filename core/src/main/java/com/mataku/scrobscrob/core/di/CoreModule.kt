package com.mataku.scrobscrob.core.di

import com.mataku.scrobscrob.core.BuildConfig
import com.mataku.scrobscrob.core.api.LastFmApiService
import com.mataku.scrobscrob.core.api.okhttp.LastfmApiAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreModule {

    private fun provideHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            engine {
                addInterceptor(LastfmApiAuthInterceptor())
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                }
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    @Singleton
    @Provides
    fun provideLastFmApiService(): LastFmApiService {
        return LastFmApiService(provideHttpClient())
    }
}