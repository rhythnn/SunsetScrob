package com.mataku.scrobscrob.app

import android.app.Application
import androidx.room.Room
import com.mataku.scrobscrob.app.model.db.AppDatabase
import com.mataku.scrobscrob.app.ui.top.TopViewModel
import com.mataku.scrobscrob.core.api.LastFmApiClient
import com.mataku.scrobscrob.core.api.repository.TopAlbumsRepository
import com.mataku.scrobscrob.core.api.repository.TopArtistsRepository
import dagger.hilt.android.HiltAndroidApp
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

@HiltAndroidApp
open class App : Application() {

    companion object {
        lateinit var database: AppDatabase
    }

    private val appModules = module {
        single { TopArtistsRepository(get()) }
        single { TopAlbumsRepository(get()) }
        single { LastFmApiClient }
        viewModel { TopViewModel(get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()
        database =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "sunset_db").build()
        startKoin {
            modules(appModules)
        }
    }
}