package com.radius.weather.data.localdb


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Weather(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "city_name") val cityName: String?,
    @ColumnInfo(name = "country") val country: String?,
    @ColumnInfo(name = "temp") val temp: String?,
    @ColumnInfo(name = "time") val time: Long?,
    @ColumnInfo(name = "url") val url: String?
)