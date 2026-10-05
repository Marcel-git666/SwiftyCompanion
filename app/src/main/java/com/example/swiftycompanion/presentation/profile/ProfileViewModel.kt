package com.example.swiftycompanion.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.example.swiftycompanion.SwiftyCompanionApplication
import com.example.swiftycompanion.common.utils.Either
import com.example.swiftycompanion.features.users.domain.UserRepository
import com.example.swiftycompanion.ui.navigation.ProfileRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    val login: String,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    sealed interface Intent {
        data object Retry : Intent
    }

    init {
        load()
    }

    fun onIntent(intent: Intent) {
        when (intent) {
            Intent.Retry -> load()
        }
    }

    private fun load() {
        _state.value = ProfileState.Loading
        viewModelScope.launch {
            _state.value = when (val result = userRepository.getUser(login)) {
                is Either.Success -> ProfileState.Loaded(result.value)
                is Either.Failure -> ProfileState.Error(result.error)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as SwiftyCompanionApplication
                val route = createSavedStateHandle().toRoute<ProfileRoute>()
                ProfileViewModel(route.login, app.container.userRepository)
            }
        }
    }
}