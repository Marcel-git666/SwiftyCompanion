package com.example.swiftycompanion.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.swiftycompanion.SwiftyCompanionApplication
import com.example.swiftycompanion.common.utils.Either
import com.example.swiftycompanion.features.users.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    sealed interface Intent {
        data class QueryChanged(val query: String) : Intent
        data object Search : Intent
        data object NavigationHandled : Intent
    }

    fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.QueryChanged -> _state.update {
                it.copy(
                    query = intent.query,
                    status = if (it.status is SearchState.Status.Error) SearchState.Status.Idle else it.status,
                )
            }
            Intent.Search -> search()
            Intent.NavigationHandled -> _state.update { it.copy(status = SearchState.Status.Idle) }
        }
    }

    private fun search() {
        val login = _state.value.query.trim().lowercase()
        if (login.isEmpty() || _state.value.status is SearchState.Status.Loading) return

        _state.update { it.copy(status = SearchState.Status.Loading) }
        viewModelScope.launch {
            val status = when (val result = userRepository.getUser(login)) {
                is Either.Success -> SearchState.Status.Found(result.value)
                is Either.Failure -> SearchState.Status.Error(result.error)
            }
            _state.update { it.copy(status = status) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as SwiftyCompanionApplication
                SearchViewModel(app.container.userRepository)
            }
        }
    }
}