package com.radius.weather.presentation.state

import com.radius.weather.domain.model.Alerts
import com.radius.weather.domain.model.Current
import com.radius.weather.domain.model.Forecast
import com.radius.weather.domain.model.Location

data class WeatherDetailState(
    val isLoading: Boolean = false,
    val current: Current? = null,
    val forecast: Forecast? = null,
    val location: Location? = null,
    val alers: Alerts? = null
)
