package com.radius.weather.data.localdb


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherRecordDao {
    @Query("SELECT * FROM Weather")
    fun getAll(): Flow<List<Weather>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weather: Weather)

    @Delete
    fun delete(weather : Weather)
}