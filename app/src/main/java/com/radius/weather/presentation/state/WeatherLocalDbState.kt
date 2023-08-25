package com.radius.weather.presentation.state

import com.radius.weather.data.localdb.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class WeatherLocalDbState(
    val isLoading: Boolean = false,
    val list: Flow<List<Weather>> = emptyFlow()
)
