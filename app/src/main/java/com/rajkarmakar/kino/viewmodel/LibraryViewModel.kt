package com.rajkarmakar.kino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajkarmakar.kino.data.model.LibrarySection
import com.rajkarmakar.kino.data.model.MediaItem
import com.rajkarmakar.kino.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class LibraryViewModel constructor(
    private val repository: MediaRepository
) : ViewModel() {

    private val _selectedSection = MutableStateFlow(LibrarySection.CONTINUE_WATCHING)
    val selectedSection: StateFlow<LibrarySection> = _selectedSection

    private val _items = MutableStateFlow<List<MediaItem>>(emptyList())
    val items: StateFlow<List<MediaItem>> = _items

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadSection(LibrarySection.CONTINUE_WATCHING)
    }

    fun onSectionSelected(section: LibrarySection) {
        _selectedSection.value = section
        loadSection(section)
    }

    private fun loadSection(section: LibrarySection) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = when (section) {
                    LibrarySection.CONTINUE_WATCHING -> repository.getContinueWatching()
                    LibrarySection.WATCHLIST -> repository.getWatchlist()
                    LibrarySection.FAVORITES -> repository.getFavorites()
                    LibrarySection.DOWNLOADS -> repository.getDownloads()
                    LibrarySection.HISTORY -> repository.getHistory()
                    LibrarySection.COMPLETED -> repository.getCompleted()
                }
                _items.value = result
            } catch (e: Exception) {
                _items.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
