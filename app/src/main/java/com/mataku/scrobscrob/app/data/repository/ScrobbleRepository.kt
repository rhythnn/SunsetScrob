package com.mataku.scrobscrob.app.data.repository

import com.mataku.scrobscrob.app.util.AppUtil
import com.mataku.scrobscrob.core.api.ApiClient
import com.mataku.scrobscrob.core.api.endpoint.ScrobbleApiResponse
import com.mataku.scrobscrob.core.api.endpoint.ScrobbleAttr
import com.mataku.scrobscrob.core.api.endpoint.ScrobbleEndpoint
import com.mataku.scrobscrob.core.entity.Track
import com.mataku.scrobscrob.core.entity.presentation.SunsetResult

class ScrobbleRepository(private val apiClient: ApiClient) {
    suspend fun scrobble(track: Track, sessionKey: String, timeStamp: Long): SunsetResult<ScrobbleAttr> {
        val params = mutableMapOf(
            "artist[0]" to track.artistName,
            "track[0]" to track.name,
            "timestamp[0]" to timeStamp.toString(),
            "album[0]" to track.albumName,
            "method" to SCROBBLE_METHOD,
            "sk" to sessionKey
        )
        val apiSig = AppUtil.generateApiSig(params)
        params.remove("method")
        params["api_sig"] = apiSig
        return try {
            val result = apiClient.post<ScrobbleApiResponse>(ScrobbleEndpoint(params = params))
            result.scrobbleResult?.let {
                SunsetResult.success(it.scrobbleAttr)
            } ?: SunsetResult.failure(Throwable())
        } catch (e: Exception) {
            SunsetResult.failure(e)
        }

    }

    companion object {
        private const val SCROBBLE_METHOD = "track.scrobble"
    }
}