package com.example.weatherapp.data.repository

import android.content.Context
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.local.DatabaseProvider
import com.example.weatherapp.data.local.WeatherDao
import com.example.weatherapp.data.model.Main
import com.example.weatherapp.data.model.Sys
import com.example.weatherapp.data.model.Weather
import com.example.weatherapp.data.model.WeatherEntity
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.model.Wind
import com.example.weatherapp.data.remote.RetrofitClient
import com.example.weatherapp.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

private const val CACHE_MAX_AGE_MS = 30 * 60 * 1000L


class WeatherRepository(context: Context) {

    private val weatherDao: WeatherDao = DatabaseProvider.getDatabase(context).weatherDao()
    private val apiService = RetrofitClient.weatherApiService
    private val apiKey = BuildConfig.OPENWEATHER_API_KEY


    fun getWeatherFlow(city: String): Flow<WeatherResponse?> =
        weatherDao.getCachedWeatherByCity(city).map { entity ->
            entity?.toWeatherResponse()
        }


    suspend fun getWeather(city: String): Result<WeatherResponse> {
        val cachedEntity = weatherDao.getCachedWeatherByCity(city).first()
        val now = System.currentTimeMillis()
        if (cachedEntity != null && (now - cachedEntity.fetchedAt) < CACHE_MAX_AGE_MS) {
            // Välimuisti voimassa → palauta Room-data, voi hakea uudestaan taustalla
            cachedEntity.toWeatherResponse().let { return Result.Success(it) }
        }

        return fetchFromApiAndSave(city)
    }

    suspend fun fetchFromApiAndSave(city: String): Result<WeatherResponse> {
        return try {
            val response = apiService.getWeather(city, apiKey)
            weatherDao.insertWeather(response.toEntity())
            Result.Success(response)
        } catch (e: IOException) {
            Result.Error(Exception("Verkkovirhe: ${e.message}"))
        } catch (e: HttpException) {
            Result.Error(Exception("Palvelinvirhe: ${e.code()}"))
        } catch (e: Exception) {
            Result.Error(Exception("Tuntematon virhe: ${e.message}"))
        }
    }

    fun getSearchHistoryFlow(): Flow<List<WeatherEntity>> =
        weatherDao.getAllCachedWeather()

    suspend fun getLatestCachedWeather(): WeatherResponse? =
        weatherDao.getLatestCachedWeather().first()?.toWeatherResponse()
}

private fun WeatherEntity.toWeatherResponse(): WeatherResponse = WeatherResponse(
    weather = listOf(Weather(id = weatherId, main = weatherMain, description = description, icon = icon)),
    main = Main(
        temp = temp,
        feels_like = feelsLike,
        tempMin = tempMin,
        tempMax = tempMax,
        pressure = pressure,
        humidity = humidity
    ),
    wind = Wind(speed = windSpeed, deg = windDeg),
    name = cityName,
    sys = Sys(country = country, sunrise = sunrise, sunset = sunset)
)

private fun WeatherResponse.toEntity(): WeatherEntity {
    val w = weather.firstOrNull() ?: throw IllegalArgumentException("weather empty")
    return WeatherEntity(
        cityName = name,
        country = sys.country,
        temp = main.temp,
        feelsLike = main.feels_like,
        tempMin = main.tempMin,
        tempMax = main.tempMax,
        weatherId = w.id,
        weatherMain = w.main,
        description = w.description,
        icon = w.icon,
        humidity = main.humidity,
        pressure = main.pressure,
        windSpeed = wind.speed,
        windDeg = wind.deg,
        sunrise = sys.sunrise,
        sunset = sys.sunset,
        fetchedAt = System.currentTimeMillis()
    )
}
