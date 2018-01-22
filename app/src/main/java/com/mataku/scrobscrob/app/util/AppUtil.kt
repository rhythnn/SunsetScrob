package com.mataku.scrobscrob.app.util

import android.util.Log
import com.mataku.scrobscrob.BuildConfig
import java.security.MessageDigest

class AppUtil {
    val apiKey = BuildConfig.API_KEY
    val sharedSecret = BuildConfig.SHARED_SECRET
    val defaultPlayingTime = 180.toLong()
    val latestScrobbleCountToDisplay = 20

    fun generateApiSig(params: MutableMap<String, String>): String {
        var str = ""
        params["api_key"] = apiKey
        params.toSortedMap().forEach { k, v ->
            str += k + v
        }
        str += sharedSecret

        val messageDigest = MessageDigest.getInstance("MD5")
        val byteData = str.toByteArray()
        messageDigest.update(byteData)
        val digest = messageDigest.digest()
        val stringBuilder = StringBuilder()
        digest.indices.forEach { i ->
            val b = (0xFF and digest[i].toInt())
            if (b < 16)
                stringBuilder.append("0")
            stringBuilder.append(Integer.toHexString(b))
        }
        return stringBuilder.toString()
    }

    fun debugLog(key: String, value: String?) {
        if (BuildConfig.DEBUG) {
            Log.d(key, value)
        }
    }
}