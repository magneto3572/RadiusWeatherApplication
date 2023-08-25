package com.radius.weather.presentation.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radius.weather.data.Resource
import com.radius.weather.data.localdb.Weather
import com.radius.weather.data.repositoryImpl.WeatherHomeRepositoryImpl
import com.radius.weather.domain.repository.WeatherHomeRepository
import com.radius.weather.presentation.MyApplication
import com.radius.weather.presentation.state.WeatherLocalDbState
import com.radius.weather.presentation.state.WeatherSearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject


@SuppressLint("MissingPermission")
@HiltViewModel
class WeatherHomeViewModel @Inject constructor(
    private var repository: WeatherHomeRepository,
    private var handler: CoroutineExceptionHandler,
) : ViewModel() {


    private var _getSearchResponse = MutableStateFlow(WeatherSearchState())
    val getSearchListItem: StateFlow<WeatherSearchState>
        get() = _getSearchResponse

    private var _getLocalDbresponse = MutableStateFlow(WeatherLocalDbState())
    val getLocalDbResponse: StateFlow<WeatherLocalDbState>
        get() = _getLocalDbresponse

    private fun getLocationFromQuery(query: String) =
        viewModelScope.launch(Dispatchers.IO + handler) {
            _getSearchResponse.update { it.copy(isLoading = true) }
            repository.getSearchResult(query).apply {
                when (this) {
                    is Resource.Success -> {
                        _getSearchResponse.update { it.copy(
                            isLoading = false,
                            list = this.value
                        ) }
                    }
                    is Resource.Failure -> {
                        Log.d("LogTagId", this.toString())
                    }
                }
            }
        }

    fun getSearchSuggestion(query: String) {
        if (query.length >= 3) {
            getLocationFromQuery(query = query)
        }
    }

    fun removeFromLocalDb(weather: Weather){
        viewModelScope.launch (Dispatchers.IO + handler) {
            repository.removeFromLocalDb(weather = weather)
        }
    }

    fun getAllDataFromLocalDb(){
        viewModelScope.launch(Dispatchers.IO + handler) {
            _getLocalDbresponse.update { it.copy(isLoading = true) }
            repository.getAllDataFromLocalDb().apply {
                _getLocalDbresponse.update {
                    it.copy(
                        isLoading = false,
                        list = it.list
                    )
                }
            }
        }

    }
    
    fun getTimeFromDate(startTime: Long?) : String{
        val d = startTime?.let { Date(it) }
        val sdf = SimpleDateFormat("hh:mm aa")
        return sdf.format(d)
    }
}