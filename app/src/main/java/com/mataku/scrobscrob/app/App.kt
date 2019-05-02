package com.mataku.scrobscrob.app

import android.app.Application
import com.mataku.scrobscrob.app.data.repository.TopAlbumsRepository
import com.mataku.scrobscrob.app.data.repository.TopArtistsRepository
import com.mataku.scrobscrob.app.ui.top.TopViewModel
import com.mataku.scrobscrob.core.api.ApiClient
import org.koin.android.ext.android.startKoin
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

open class App : Application() {

    private val appModules = module {
        single { TopArtistsRepository(get()) }
        single { TopAlbumsRepository(get()) }
        single { ApiClient }
        viewModel { TopViewModel(get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(
            this,
            listOf(appModules)
        )
    }
}