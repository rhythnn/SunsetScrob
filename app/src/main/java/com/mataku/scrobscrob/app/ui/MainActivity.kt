package com.mataku.scrobscrob.app.ui

import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
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

    private val settingsRequestCode = 1001

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
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    //    右上のメニューボタン表示
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.list, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            settingsRequestCode -> {
                setUpContentTab()
            }
        }
    }

    //    メニューボタンのクリックイベントを定義
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.overflow_options -> {
                showSettings()
                return true
            }
            else -> {
                showSettings()
                return true
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
        startActivityForResult(intent, settingsRequestCode)
    }

    private fun setUpContentTab() {
        val adapter = object : ContentsAdapter(this.supportFragmentManager) {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
//                        self.supportActionBar?.show()
                        self.title = "Latest 20 scrobbles (Beta)"
                    }
                    1 -> {
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
        val tabLayout = binding.activityMainTablayout
        tabLayout.also {
            it.setupWithViewPager(viewPager)
            val scrobbleTab = it.getTabAt(0)
            scrobbleTab?.setIcon(R.drawable.ic_last_fm_logo)

            val albumsTab = it.getTabAt(1)
            albumsTab?.setIcon(R.drawable.ic_album_black_24px)

            val artistsTab = it.getTabAt(2)
            artistsTab?.setIcon(R.drawable.ic_account_circle_black)
        }

    }
}