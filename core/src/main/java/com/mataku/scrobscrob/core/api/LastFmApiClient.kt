package com.mataku.scrobscrob.core.api

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.mataku.scrobscrob.core.BuildConfig
import com.mataku.scrobscrob.core.api.endpoint.Endpoint
import com.mataku.scrobscrob.core.api.okhttp.LastfmApiAuthInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.request
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor

@ExperimentalCoroutinesApi
class LastFmApiClient(val context: Context) {
    companion object {
        const val BASE_URL = "https://ws.audioscrobbler.com"
    }

    val client: HttpClient
        get() {
            return HttpClient(OkHttp) {
                engine {
                    addInterceptor(LastfmApiAuthInterceptor())
                    response.apply {
                        charset("UTF_8")
                    }

                    if (BuildConfig.DEBUG) {
                        val logging = HttpLoggingInterceptor()
                        logging.level = HttpLoggingInterceptor.Level.BODY
                        addInterceptor(logging)
                        val networkPlugin = AndroidFlipperClient.getInstance(context)
                            .getPlugin<NetworkFlipperPlugin>(NetworkFlipperPlugin.ID)
                        addNetworkInterceptor(FlipperOkhttpInterceptor(networkPlugin))
                    }
                }
                install(JsonFeature) {
                    serializer = KotlinxSerializer(Json.nonstrict)
                }
            }
        }

    suspend inline fun <reified T> request(endpoint: Endpoint): T {
        val response = client.request<T>(BASE_URL + endpoint.path) {
            method = endpoint.requestType
            if (endpoint.params.isNotEmpty()) {
                endpoint.params.forEach { (k, v) ->
                    parameter(key = k, value = v)
                }
            }
        }
        client.close()
        return response
    }

    suspend inline fun <reified T> get(endpoint: Endpoint): T {
        val response = client.get<T>(BASE_URL + endpoint.path) {
            if (endpoint.params.isNotEmpty()) {
                endpoint.params.forEach { (k, v) ->
                    parameter(key = k, value = v)
                }
            }
        }
        client.close()
        return response
    }

    suspend inline fun <reified T> getAsFlow(endpoint: Endpoint): Flow<T> {
        val response = flowOf(client.get<T>(BASE_URL + endpoint.path) {
            if (endpoint.params.isNotEmpty()) {
                endpoint.params.forEach { (k, v) ->
                    parameter(key = k, value = v)
                }
            }
        })
        client.close()
        return response
    }

    suspend inline fun <reified T> post(endpoint: Endpoint): T {
        val response = client.post<T>(BASE_URL + endpoint.path) {
            // Fix at ktor 1.2
            // https://github.com/ktorio/ktor/issues/904
            body = ""
            endpoint.params.forEach { (k, v) ->
                parameter(k, v)
            }
        }

        client.close()
        return response
    }
}