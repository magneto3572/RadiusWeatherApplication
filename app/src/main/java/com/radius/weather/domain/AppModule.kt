package com.radius.weather.domain

import android.content.Context
import androidx.room.Room
import com.radius.weather.data.RemoteDataSource
import com.radius.weather.data.apiCall.ApiInterface
import com.radius.weather.data.localdb.AppRoomDatabase
import com.radius.weather.data.localdb.Weather
import com.radius.weather.data.localdb.WeatherRecordDao
import com.radius.weather.data.repositoryImpl.WeatherDetailsRepositoryImpl
import com.radius.weather.data.repositoryImpl.WeatherHomeRepositoryImpl
import com.radius.weather.domain.repository.WeatherDetailsRepository
import com.radius.weather.domain.repository.WeatherHomeRepository
import com.radius.weather.presentation.MyApplication
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppRoomDatabase {
        return Room.databaseBuilder(
                    appContext,
                    AppRoomDatabase::class.java,
                    "WeatherRecordDatabase"
                ).fallbackToDestructiveMigration()
                    .build()
    }

    @Singleton
    @Provides
    fun provideChannelDao(appDatabase: AppRoomDatabase): WeatherRecordDao {
        return appDatabase.weatherRecordDao()
    }

    @Provides
    @Singleton
    fun providesApi(remoteDataSource: RemoteDataSource, @ApplicationContext context: Context): ApiInterface {
        return remoteDataSource.buildApi(ApiInterface::class.java, context)
    }

//    @Provides
//    @Singleton
//    fun provideWeatherDetailsRepositoryImpl(remoteDataSource: RemoteDataSource, @ApplicationContext context: Context, appDatabase: AppRoomDatabase): WeatherDetailsRepositoryImpl {
//        return WeatherDetailsRepositoryImpl(providesApi(remoteDataSource, context), provideChannelDao(appDatabase))
//    }
//
//    @Provides
//    @Singleton
//    fun provideWeatherHomeRepositoryImpl(remoteDataSource: RemoteDataSource, @ApplicationContext context: Context, appDatabase: AppRoomDatabase): WeatherHomeRepositoryImpl {
//        return WeatherHomeRepositoryImpl(providesApi(remoteDataSource, context), provideChannelDao(appDatabase))
//    }

    @Singleton
    @Provides
    fun exceptionHandler() : CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
        }
    }

    @Provides
    fun getApplication() : MyApplication {
        return MyApplication()
    }
}