package com.radius.weather.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radius.weather.data.Resource
import com.radius.weather.data.localdb.Weather
import com.radius.weather.domain.repository.WeatherDetailsRepository
import com.radius.weather.presentation.state.WeatherDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject


@HiltViewModel
class WeatherDetailsViewModel @Inject constructor(
    private var repository: WeatherDetailsRepository,
    private var handler: CoroutineExceptionHandler,
) : ViewModel() {

    private var _getForecast = MutableStateFlow(WeatherDetailState())
    val getForeCast: StateFlow<WeatherDetailState>
        get() = _getForecast

    fun getForecastFromApi(q: String) =
        viewModelScope.launch(Dispatchers.IO + handler) {
            _getForecast.update { it.copy(isLoading = true) }
            repository.getForecastFromApi(q,5, "no", "yes").apply {
                when (this) {
                    is Resource.Success -> {
                        _getForecast.update {
                            it.copy(
                                isLoading = false,
                                current = this.value.current,
                                forecast = this.value.forecast,
                                location = this.value.location,
                                alers = this.value.alerts
                            )
                        }
                    }
                    is Resource.Failure -> {
                        Log.d("LogTagId", this.toString())
                    }
                }
            }
        }

    fun insertIntoLocalDb(weather: Weather){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insertWeatherDetailsIntoLocalDb(weather = weather)
            }catch (e :Exception){
                e.printStackTrace()
            }
        }
    }

    fun getTimeInHour(timeEpoch: Int): Int {
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = timeEpoch * 1000L
        cal.timeZone = TimeZone.getTimeZone("UTC")
        return cal.get(Calendar.HOUR_OF_DAY)
    }

    fun parseDayOfWeek(timeEpoch: Int): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeEpoch * 1000L
        calendar.timeZone = TimeZone.getTimeZone("UTC")

        return when(calendar.get(Calendar.DAY_OF_WEEK)){
            1-> return "Sun"
            2-> return "Mon"
            3-> return "Tue"
            4 -> return "Wed"
            5 -> return "Thu"
            6 -> return "Fri"
            7 -> return "Sat"
            else -> ""
        }
    }
}