package com.rajkarmakar.kino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajkarmakar.kino.data.model.ContentType
import com.rajkarmakar.kino.data.model.MediaItem
import com.rajkarmakar.kino.data.repository.MediaRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)

class SearchViewModel constructor(
    private val repository: MediaRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MediaItem>>(emptyList())
    val searchResults: StateFlow<List<MediaItem>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _trendingSearches = MutableStateFlow<List<String>>(
        listOf("Dune: Part Two", "Jujutsu Kaisen", "Stranger Things", "Pushpa 2", "Demon Slayer")
    )
    val trendingSearches: StateFlow<List<String>> = _trendingSearches.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private val _selectedTab = MutableStateFlow<ContentType?>(null)
    val selectedTab: StateFlow<ContentType?> = _selectedTab.asStateFlow()

    init {
        viewModelScope.launch {
            _query
                .debounce(300)
                .filter { it.length >= 2 }
                .collect { searchQuery ->
                    performSearch(searchQuery)
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        if (newQuery.isEmpty()) {
            _searchResults.value = emptyList()
        }
    }

    fun search() {
        if (_query.value.length >= 2) {
            performSearch(_query.value)
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            try {
                val results = repository.search(query, _selectedTab.value)
                _searchResults.value = results
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun onTabSelected(type: ContentType?) {
        _selectedTab.value = type
        if (_query.value.length >= 2) {
            performSearch(_query.value)
        }
    }
}
