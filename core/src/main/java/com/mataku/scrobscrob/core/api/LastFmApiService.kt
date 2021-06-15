package com.mataku.scrobscrob.core.api

import com.mataku.scrobscrob.core.api.endpoint.Endpoint
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.request
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class LastFmApiService(val client: HttpClient) {
    companion object {
        const val BASE_URL = "https://ws.audioscrobbler.com"
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