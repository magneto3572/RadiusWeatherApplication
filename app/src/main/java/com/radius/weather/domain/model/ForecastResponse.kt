package com.radius.weather.domain.model

data class ForecastResponse(
    val alerts: Alerts,
    val current: Current,
    val forecast: Forecast,
    val location: Location
)