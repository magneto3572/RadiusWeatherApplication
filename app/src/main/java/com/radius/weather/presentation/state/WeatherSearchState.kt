package com.radius.weather.presentation.state

import com.radius.weather.domain.model.SearchLocationResponseItem

data class WeatherSearchState(
    val isLoading: Boolean = false,
    val list: List<SearchLocationResponseItem> = emptyList()
)
