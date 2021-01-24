package com.mataku.scrobscrob.app

import android.app.Application
import androidx.room.Room
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import com.mataku.scrobscrob.BuildConfig
import com.mataku.scrobscrob.app.model.db.AppDatabase
import com.mataku.scrobscrob.app.ui.top.TopViewModel
import com.mataku.scrobscrob.core.api.LastFmApiClient
import com.mataku.scrobscrob.core.api.repository.TopAlbumsRepository
import com.mataku.scrobscrob.core.api.repository.TopArtistsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

@ExperimentalCoroutinesApi
open class App : Application() {

    companion object {
        lateinit var database: AppDatabase
    }

    private val appModules = module {
        single { TopArtistsRepository(get()) }
        single { TopAlbumsRepository(get()) }
        single { LastFmApiClient(this.androidContext()) }
        viewModel { TopViewModel(get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()
        database =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "sunset_db").build()
        startKoin {
            androidContext(this@App)
            modules(appModules)
        }
        SoLoader.init(this, false)
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(NetworkFlipperPlugin())
            client.start()
        }
    }
}