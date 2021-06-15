package com.mataku.scrobscrob.core.api.repository

import com.mataku.scrobscrob.core.api.LastFmApiService
import com.mataku.scrobscrob.core.api.endpoint.AuthMobileSessionApiResponse
import com.mataku.scrobscrob.core.api.endpoint.AuthMobileSessionEndpoint
import com.mataku.scrobscrob.core.api.endpoint.MobileSession
import com.mataku.scrobscrob.core.entity.presentation.SunsetResult
import com.mataku.scrobscrob.core.util.AppUtil
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileSessionRepository @Inject constructor(val apiService: LastFmApiService) {
    suspend fun authorize(userName: String, password: String): SunsetResult<MobileSession> {
        val params = mutableMapOf(
            "username" to userName,
            "password" to password,
            "method" to METHOD
        )
        val apiSig = AppUtil().generateApiSig(params)
        params["api_sig"] = apiSig

        return try {
            val result =
                apiService.post<AuthMobileSessionApiResponse>(AuthMobileSessionEndpoint(params = params))
            result.mobileSession?.let {
                SunsetResult.success(it)
            } ?: SunsetResult.failure(Throwable())
        } catch (e: Exception) {
            SunsetResult.failure(e)
        }
    }

    companion object {
        private const val METHOD = "auth.getMobileSession"
    }
}