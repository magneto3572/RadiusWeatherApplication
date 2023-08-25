package com.radius.weather.data.localdb


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Weather::class], version = 1, exportSchema = false)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun weatherRecordDao(): WeatherRecordDao
}