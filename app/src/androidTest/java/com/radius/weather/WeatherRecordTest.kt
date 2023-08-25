package com.radius.weather

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.radius.weather.data.localdb.AppRoomDatabase
import com.radius.weather.data.localdb.Weather
import com.radius.weather.data.localdb.WeatherRecordDao
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherRecordTest {

    private lateinit var database: AppRoomDatabase
    private lateinit var dao : WeatherRecordDao

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppRoomDatabase::class.java).allowMainThreadQueries().build()

        dao = database.weatherRecordDao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun insertShoppingItem() = runTest {
        val item = Weather(1, "test", "india", "test", 0L,"test" )
        dao.insert(item)
        dao.getAll().test {
            assertThat(awaitItem()).contains(item)
        }
    }

    @Test
    fun deleteShoppingItem() = runTest {
        val item = Weather(1, "test12", "india67", "testyt", 0L,"te5st" )
        dao.insert(item)
        dao.delete(item)
        dao.getAll().test {
            assertThat(awaitItem()).doesNotContain(item)
        }
    }
}