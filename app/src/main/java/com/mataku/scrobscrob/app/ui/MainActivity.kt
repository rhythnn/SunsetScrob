package com.mataku.scrobscrob.app.ui

import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.FrameMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import com.mataku.scrobscrob.R
import com.mataku.scrobscrob.app.data.Migration
import com.mataku.scrobscrob.app.receiver.AppleMusicNotificationReceiver
import com.mataku.scrobscrob.app.ui.adapter.ContentsAdapter
import com.mataku.scrobscrob.app.ui.view.MainViewCallback
import com.mataku.scrobscrob.app.util.SharedPreferencesHelper
import com.mataku.scrobscrob.databinding.ActivityMainBinding
import io.realm.Realm
import io.realm.RealmConfiguration


class MainActivity : AppCompatActivity(), MainViewCallback {
    private var receiver = AppleMusicNotificationReceiver()
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var binding: ActivityMainBinding

    private val self = this

    private lateinit var metrics: FrameMetrics

    val runnable = Runnable {
        Log.d("MATAKUDEBUG Metrics", "ANIMATION_DURATION: " + metrics.getMetric((FrameMetrics.ANIMATION_DURATION)) / Math.pow(10.0, 6.0))
        Log.d("MATAKUDEBUG Metrics", "COMMAND_ISSUE_DURATION: " + metrics.getMetric(FrameMetrics.COMMAND_ISSUE_DURATION) / Math.pow(10.0, 6.0))
        Log.d("MATAKUDEBUG Metrics", "DRAW_DURATION: " + metrics.getMetric(FrameMetrics.DRAW_DURATION) / Math.pow(10.0, 6.0))
        Log.d("MATAKUDEBUG Metrics", "FIRST_DRAW_FRAME: " + metrics.getMetric(FrameMetrics.FIRST_DRAW_FRAME) / Math.pow(10.0, 6.0))
        Log.d("MATAKUDEBUG Metrics", "INPUT_HANDLING_DURATION: " + metrics.getMetric(FrameMetrics.INPUT_HANDLING_DURATION) / Math.pow(10.0, 6.0))
        Log.d("MATAKUDEBUG Metrics", "LAYOUT_MEASURE_DURATION: " + metrics.getMetric(FrameMetrics.LAYOUT_MEASURE_DURATION) / Math.pow(10.0, 6.0))
        Log.d("MATAKUDEBUG Metrics", "SWAP_BUFFERS_DURATION: " + metrics.getMetric(FrameMetrics.SWAP_BUFFERS_DURATION) / Math.pow(10.0, 6.0))
        Log.d("MATAKUDEBUG Metrics", "SYNC_DURATION: " + metrics.getMetric(FrameMetrics.SYNC_DURATION) / Math.pow(10.0, 6.0))
        Log.d("MATAKUDEBUG Metrics", "TOTAL_DURATION: " + metrics.getMetric(FrameMetrics.TOTAL_DURATION) / Math.pow(10.0, 6.0))
        Log.d("MATAKUDEBUG Metrics", "UNKNOWN_DELAY_DURATION: " + metrics.getMetric(FrameMetrics.UNKNOWN_DELAY_DURATION) / Math.pow(10.0, 6.0))
    }

    private val frameMetricsHandler = Handler()

    private val frameMetricsAvailableListener = Window.OnFrameMetricsAvailableListener { _, frameMetrics, _ ->
        val frameMetricsCopy = FrameMetrics(frameMetrics)
        // Layout measure duration in Nano seconds
        val layoutMeasureDurationNs = frameMetricsCopy.getMetric(FrameMetrics.LAYOUT_MEASURE_DURATION)

        Log.d("MATAKUDEBUG", "layoutMeasureDurationNs: $layoutMeasureDurationNs")
    }


    val listener = Window.OnFrameMetricsAvailableListener { window, frameMetrics, dropCountSinceLastInvocation ->
        Log.i("MATAKUDEBUG", "onFrame")
        metrics = FrameMetrics(frameMetrics)
    }

    val handler = Handler()

    override fun onResume() {
        super.onResume()
//        window.addOnFrameMetricsAvailableListener(frameMetricsAvailableListener, frameMetricsHandler)
        window.addOnFrameMetricsAvailableListener(listener, frameMetricsHandler)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Realm.init(this)
        val builder = RealmConfiguration.Builder()
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        builder.schemaVersion(1L).migration(Migration())
        val config = builder.build()
        Realm.setDefaultConfiguration(config)
        this.title = "Latest 20 scrobbles (Beta)"
//        if (BuildConfig.DEBUG) {
//            val realm = Realm.getDefaultInstance()
//            realm.executeTransaction {
//                realm.deleteAll()
//            }
//        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val filter = IntentFilter()
        filter.addAction("AppleMusic")
        registerReceiver(receiver, filter)
        setUpContentTab()
        handler.postDelayed(runnable, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        window.removeOnFrameMetricsAvailableListener(listener)
//        window.removeOnFrameMetricsAvailableListener(frameMetricsAvailableListener)
    }

    //    右上のメニューボタン表示
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.list, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SETTINGS_REQUEST_CODE -> {
                setUpContentTab()
            }
        }
    }

    //    メニューボタンのクリックイベントを定義
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.overflow_options -> {
                showSettings()
                true
            }
            else -> {
                showSettings()
                true
            }
        }
    }

    override fun showNotificationAccessSettingMenu() {
        val intent = Intent()
        intent.action = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
        startActivity(intent)
    }

    override fun showLoginActivity() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showSettings() {
        val intent = Intent(applicationContext, SettingsActivity::class.java)
        startActivityForResult(intent, SETTINGS_REQUEST_CODE)
    }

    private fun setUpContentTab() {
        val adapter = object : ContentsAdapter(supportFragmentManager) {
            override fun onPageSelected(position: Int) {
                when (position) {
                    ContentsAdapter.SCROBBLE_POSITION -> {
//                        self.supportActionBar?.show()
                        self.title = "Latest 20 scrobbles (Beta)"
                    }
                    ContentsAdapter.TOP_ALBUM_POSITION -> {
//                        self.supportActionBar?.hide()
                        self.title = "Top Albums"
                    }
                    else -> {
                        self.title = "Top Artists"
                    }
                }
            }
        }

        val viewPager = binding.activityMainViewpager
        viewPager.also {
            it.adapter = adapter
            it.addOnPageChangeListener(adapter)
        }
        val navItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_scrobble -> {
                    viewPager.currentItem = ContentsAdapter.SCROBBLE_POSITION
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_top_albums -> {
                    viewPager.currentItem = ContentsAdapter.TOP_ALBUM_POSITION
                    return@OnNavigationItemSelectedListener true
                }

                R.id.menu_top_artists -> {
                    viewPager.currentItem = ContentsAdapter.TOP_ARTISTS_POSITION
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
        binding.activityMainTablayout.setOnNavigationItemSelectedListener(navItemSelectedListener)
    }

    companion object {
        const val SETTINGS_REQUEST_CODE = 1001
    }
}