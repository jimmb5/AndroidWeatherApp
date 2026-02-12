package com.example.weatherapp.ui.screens.weather

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.ui.components.ErrorScreen
import com.example.weatherapp.ui.components.SearchBar
import com.example.weatherapp.ui.components.WeatherContent
import com.example.weatherapp.util.Result

// Päänäkymä: kokoaa SearchBarin ja sisällön yhteen.
// collectAsState() = kuuntele StateFlow:ta ja päivitä UI automaattisesti.
@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val weatherState by viewModel.weatherState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.onSearchQueryChange(it) },
            onSearch = { viewModel.searchWeather() }
        )

        when (val state = weatherState) {
            is Result.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Result.Success -> {
                WeatherContent(weather = state.data)
            }
            is Result.Error -> {
                ErrorScreen(
                    message = state.exception.message ?: "Virhe",
                    onRetry = { viewModel.searchWeather() }
                )
            }
        }
    }
}

