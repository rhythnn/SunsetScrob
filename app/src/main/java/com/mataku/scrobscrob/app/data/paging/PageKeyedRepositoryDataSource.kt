package com.mataku.scrobscrob.app.data.paging

import androidx.paging.PageKeyedDataSource
import com.mataku.scrobscrob.app.data.repository.TopArtistsRepository
import com.mataku.scrobscrob.core.entity.Artist
import com.mataku.scrobscrob.core.entity.presentation.Result
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PageKeyedTopArtistsDataSource(val repository: TopArtistsRepository, val userName: String) :
    PageKeyedDataSource<Int, Artist>() {

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Artist>) {

    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Artist>) {
        requestWithPage(1, params.requestedLoadSize) { artists, next ->
            callback.onResult(artists, null, next)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Artist>) {
        requestWithPage(params.key, params.requestedLoadSize) { artists, next ->
            callback.onResult(artists, next)
        }
    }

    private fun requestWithPage(page: Int, per: Int, callback: (data: List<Artist>, next: Int?) -> Unit) {
        GlobalScope.launch {
            val result = repository.topArtistsResponse(
                page = page,
                userName = userName
            )
            var next: Int? = null
            when (result) {
                is Result.Success -> {
                    if (result.data.isNotEmpty() && result.data.size == per) {
                        callback(result.data, page + 1)
                    } else {
                        callback(result.data, next)
                    }
                }
                else -> {
                    callback(emptyList(), next)
                }
            }
        }
    }
}