package com.mataku.scrobscrob.app.di

import com.mataku.scrobscrob.core.api.LastFmApiService
import com.mataku.scrobscrob.core.api.repository.MobileSessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideMobileSessionRepository(
        lastFmApiService: LastFmApiService
    ): MobileSessionRepository {
        return MobileSessionRepository(lastFmApiService)
    }
}