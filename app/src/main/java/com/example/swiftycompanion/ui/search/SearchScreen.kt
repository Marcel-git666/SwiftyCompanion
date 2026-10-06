package com.example.swiftycompanion.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swiftycompanion.BuildConfig
import com.example.swiftycompanion.R
import com.example.swiftycompanion.features.users.domain.models.User
import com.example.swiftycompanion.features.users.errors.UserError
import com.example.swiftycompanion.presentation.search.SearchState
import com.example.swiftycompanion.presentation.search.SearchViewModel
import com.example.swiftycompanion.ui.common.messageRes
import com.example.swiftycompanion.ui.theme.SwiftyCompanionTheme

@Composable
fun SearchScreen(
    onUserFound: (User) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val found = state.status as? SearchState.Status.Found
    LaunchedEffect(found) {
        if (found != null) {
            onUserFound(found.user)
            viewModel.onIntent(SearchViewModel.Intent.NavigationHandled)
        }
    }

    SearchContent(state = state, onIntent = viewModel::onIntent, modifier = modifier)
}

@Composable
fun SearchContent(
    state: SearchState,
    onIntent: (SearchViewModel.Intent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLoading = state.status is SearchState.Status.Loading
    val canSearch = state.query.isNotBlank() && !isLoading
    val errorMessage = when (val status = state.status) {
        is SearchState.Status.Error -> stringResource(status.error.messageRes())
        SearchState.Status.InvalidLogin -> stringResource(R.string.error_invalid_login)
        else -> null
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.search_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            OutlinedTextField(
                value = state.query,
                onValueChange = { onIntent(SearchViewModel.Intent.QueryChanged(it)) },
                label = { Text(stringResource(R.string.search_login_label)) },
                singleLine = true,
                enabled = !isLoading,
                isError = errorMessage != null,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Search,
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { if (canSearch) onIntent(SearchViewModel.Intent.Search) },
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { onIntent(SearchViewModel.Intent.Search) },
                enabled = canSearch,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.search_button))
                }
            }
            if (BuildConfig.DEBUG) {
                TextButton(onClick = { onIntent(SearchViewModel.Intent.SimulateTokenExpiry) }) {
                    Text(stringResource(R.string.debug_simulate_token_expiry))
                }
            }
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchContentErrorPreview() {
    SwiftyCompanionTheme {
        SearchContent(
            state = SearchState(query = "lform", status = SearchState.Status.Error(UserError.NotFound)),
            onIntent = {},
        )
    }
}