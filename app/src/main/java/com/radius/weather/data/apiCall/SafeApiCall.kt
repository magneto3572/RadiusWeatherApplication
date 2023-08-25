package com.radius.weather.data.apiCall


import android.util.Log
import com.radius.weather.data.Resource
import retrofit2.HttpException


interface SafeApiCall {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return runCatching {
            Resource.Success(apiCall.invoke())
        }.getOrElse { throwable ->
            when (throwable) {
                is HttpException -> {
                    Resource.Failure(false, throwable.code(),  throwable.response()?.errorBody())
                }
                else -> {
                    Log.d("failure","${throwable.message}")
                    Resource.Failure(true, null, null)
                }
            }
        }
    }
}