package com.jenshen.smartmirror.manager.api.weather

import com.jenshen.smartmirror.data.api.weather.WeatherApi
import com.jenshen.smartmirror.data.api.weather.WeatherApi.Companion.METRIC_FORMAT
import com.jenshen.smartmirror.data.entity.weather.day.WeatherForCurrentDayResponse
import com.jenshen.smartmirror.data.entity.weather.forecast.WeatherForecastResponse
import com.jenshen.smartmirror.manager.preference.PreferencesManager
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleSource
import java.util.*


class WeatherApiManager(private val weatherApi: WeatherApi, private val preferencesManager: PreferencesManager) : IWeatherApiManager {

    companion object {
        val MAX_DIFFERENCE = 60 * 60 * 1000
    }

    override fun getCurrentWeather(lat: Double, lon: Double): Flowable<WeatherForCurrentDayResponse> {
        val data = preferencesManager.getCurrentWeather()
        val singles = mutableListOf<SingleSource<WeatherForCurrentDayResponse>>()
        val dataFromCacheSingle = Single.fromCallable { data!! }
        val dataFromApiSingle = weatherApi.getCurrentWeatherForCity(lat, lon, METRIC_FORMAT, Locale.getDefault().language)
                .doOnSuccess { preferencesManager.saveCurrentWeather(it) }

        if (data != null) {
            singles.add(dataFromCacheSingle)
            val currentTime = Calendar.getInstance().time.time
            val responseCalculationTime = data.date.time
            val difference = currentTime - responseCalculationTime
            if (difference >= MAX_DIFFERENCE) {
                singles.add(dataFromApiSingle)
            }
        } else {
            singles.add(dataFromApiSingle)
        }
        return Single.merge(singles)
    }

    override fun getWeatherForecast(lat: Double, lon: Double): Flowable<WeatherForecastResponse> {
        val data = preferencesManager.getWeatherForecast()
        val singles = mutableListOf<SingleSource<WeatherForecastResponse>>()
        val dataFromCacheSingle = Single.fromCallable { data!! }
        val dataFromApiSingle = weatherApi.getWeatherForecastForCity(lat, lon, METRIC_FORMAT, Locale.getDefault().language)
                .doOnSuccess { preferencesManager.saveWeatherForecast(it) }

        if (data != null) {
            singles.add(dataFromCacheSingle)
            val currentTime = Calendar.getInstance().time.time
            val responseCalculationTime = data.weathersList.iterator().next().date.time
            val difference = currentTime - responseCalculationTime
            if (difference >= MAX_DIFFERENCE) {
                singles.add(dataFromApiSingle)
            }
        } else {
            singles.add(dataFromApiSingle)
        }
        return Single.merge(singles)
    }
}