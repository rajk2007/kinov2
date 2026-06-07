package com.rajkarmakar.kino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajkarmakar.kino.data.model.KinoTheme
import com.rajkarmakar.kino.data.model.UserProfile
import com.rajkarmakar.kino.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _user = MutableStateFlow(
        UserProfile(
            id = "1",
            name = "Raj Karmakar",
            isPremium = true,
            membershipStatus = "Premium Member"
        )
    )
    val user: StateFlow<UserProfile> = _user

    private val _selectedTheme = MutableStateFlow(KinoTheme.AMOLED_BLACK)
    val selectedTheme: StateFlow<KinoTheme> = _selectedTheme

    private val _showThemeDialog = MutableStateFlow(false)
    val showThemeDialog: StateFlow<Boolean> = _showThemeDialog

    init {
        viewModelScope.launch {
            preferencesManager.getTheme().collect { theme ->
                _selectedTheme.value = theme
            }
        }
    }

    fun showThemeSelector() {
        _showThemeDialog.value = true
    }

    fun hideThemeSelector() {
        _showThemeDialog.value = false
    }

    fun onThemeSelected(theme: KinoTheme) {
        viewModelScope.launch {
            preferencesManager.setTheme(theme)
            _selectedTheme.value = theme
            _showThemeDialog.value = false
        }
    }
}
