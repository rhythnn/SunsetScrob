package com.mataku.scrobscrob.app.ui.top

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mataku.scrobscrob.app.data.repository.TopAlbumsRepository
import com.mataku.scrobscrob.app.data.repository.TopArtistsRepository
import com.mataku.scrobscrob.core.api.endpoint.Artist
import com.mataku.scrobscrob.core.entity.Album
import com.mataku.scrobscrob.core.entity.presentation.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TopViewModel(
    private val topAlbumRepository: TopAlbumsRepository,
    private val topArtistsRepository: TopArtistsRepository
) : ViewModel(), CoroutineScope {

    val topAlbumsResult = MutableLiveData<Result<List<Album>>>()
    val topArtistsResult = MutableLiveData<Result<List<Artist>>>()

    private val job = Job()
    override val coroutineContext: CoroutineContext get() = job

    fun loadAlbums(page: Int, userName: String) {
        launch(coroutineContext) {
            val result = async {
                topAlbumRepository.topAlbumsResponse(page, userName)
            }
            topAlbumsResult.postValue(result.await())
        }
    }

    fun loadArtists(page: Int, userName: String) {
        launch(coroutineContext) {
            val result = async {
                topArtistsRepository.topArtistsResponse(page, userName)
            }
            topArtistsResult.postValue(result.await())
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}