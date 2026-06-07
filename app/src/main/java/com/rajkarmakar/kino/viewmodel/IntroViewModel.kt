package com.rajkarmakar.kino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajkarmakar.kino.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _shouldShowIntro = MutableStateFlow(true)
    val shouldShowIntro: StateFlow<Boolean> = _shouldShowIntro

    init {
        viewModelScope.launch {
            _shouldShowIntro.value = preferencesManager.shouldShowIntro()
        }
    }

    fun markIntroShown() {
        viewModelScope.launch {
            preferencesManager.setIntroShown()
        }
    }

    fun replayIntro() {
        viewModelScope.launch {
            preferencesManager.setReplayIntro(true)
            _shouldShowIntro.value = true
        }
    }
}
