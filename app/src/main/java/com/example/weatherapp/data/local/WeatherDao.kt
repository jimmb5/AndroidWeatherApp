package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.model.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather_cache ORDER BY fetchedAt DESC")
    fun getAllCachedWeather(): Flow<List<WeatherEntity>>

    @Query("SELECT * FROM weather_cache WHERE cityName = :city LIMIT 1")
    fun getCachedWeatherByCity(city: String): Flow<WeatherEntity?>

    @Query("SELECT * FROM weather_cache ORDER BY fetchedAt DESC LIMIT 1")
    fun getLatestCachedWeather(): Flow<WeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(entity: WeatherEntity)

    @Query("DELETE FROM weather_cache")
    suspend fun clearAll()
}
