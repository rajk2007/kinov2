package com.rajkarmakar.kino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajkarmakar.kino.data.model.MediaItem
import com.rajkarmakar.kino.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class DetailsViewModel constructor(
    private val repository: MediaRepository
) : ViewModel() {

    private val _media = MutableStateFlow<MediaItem?>(null)
    val media: StateFlow<MediaItem?> = _media

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _showAdultWarning = MutableStateFlow(false)
    val showAdultWarning: StateFlow<Boolean> = _showAdultWarning

    fun loadMedia(mediaId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val item = repository.getMediaDetails(mediaId)
                _media.value = item
                if (item.isAdult) {
                    _showAdultWarning.value = true
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun dismissAdultWarning() {
        _showAdultWarning.value = false
    }
}
