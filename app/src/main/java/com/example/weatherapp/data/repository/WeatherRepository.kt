package com.example.weatherapp.data.repository

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.remote.RetrofitClient
import com.example.weatherapp.util.Result
import retrofit2.HttpException
import java.io.IOException

// Repository kapseloi kaiken datan hakemisen.
// ViewModel ei tiedä mistä data tulee (API, tietokanta, välimuisti).
class WeatherRepository {

    // Käytetään RetrofitClientin singleton-instanssia
    private val apiService = RetrofitClient.weatherApiService

    // API-avain BuildConfigista (turvallinen tapa, ks. Vaihe 0)
    // Avain luetaan local.properties → build.gradle.kts → BuildConfig
    private val apiKey = BuildConfig.OPENWEATHER_API_KEY

    // suspend = tämä funktio voi "pysähtyä" odottamaan vastausta
    // ilman säikeen blokkausta (Kotlin Coroutines)
    suspend fun getWeather(city: String): Result<WeatherResponse> {
        return try {
            // Kutsutaan API:a - Retrofit hoitaa HTTP-pyynnön
            val response = apiService.getWeather(city, apiKey)
            Result.Success(response)     // Palautetaan onnistunut tulos
        } catch (e: IOException) {
            // Verkkovirhe (ei nettiyhteyttä, timeout)
            Result.Error(Exception("Verkkovirhe: ${e.message}"))
        } catch (e: HttpException) {
            // HTTP-virhe (404 Not Found, 500 Server Error)
            Result.Error(Exception("Palvelinvirhe: ${e.code()}"))
        } catch (e: Exception) {
            // Muu virhe
            Result.Error(Exception("Tuntematon virhe: ${e.message}"))
        }
    }
}

