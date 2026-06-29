package com.example.findinglogs.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.findinglogs.model.model.City
import com.example.findinglogs.model.model.ForecastItem
import com.example.findinglogs.model.model.GeoResult
import com.example.findinglogs.model.model.Weather
import com.example.findinglogs.model.repo.Repository
import com.example.findinglogs.model.repo.remote.api.WeatherCallback
import java.util.concurrent.atomic.AtomicInteger

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(application)

    private val _weatherList = MutableLiveData<List<Weather>>(emptyList())
    val weatherList: LiveData<List<Weather>> = _weatherList

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _cities = MutableLiveData(repository.getCities())
    val cities: LiveData<List<City>> = _cities

    private val _searchResults = MutableLiveData<List<GeoResult>>(emptyList())
    val searchResults: LiveData<List<GeoResult>> = _searchResults

    private val _isSearching = MutableLiveData(false)
    val isSearching: LiveData<Boolean> = _isSearching

    private val _forecastItems = MutableLiveData<List<ForecastItem>>(emptyList())
    val forecastItems: LiveData<List<ForecastItem>> = _forecastItems

    private val handler = Handler(Looper.getMainLooper())
    private val fetchRunnable = Runnable { fetchAllForecasts() }

    init {
        fetchAllForecasts()
    }

    fun refresh() {
        fetchAllForecasts()
    }

    private fun fetchAllForecasts() {
        handler.removeCallbacks(fetchRunnable)
        _isLoading.value = true
        val cityList = repository.getCities()
        if (cityList.isEmpty()) {
            _isLoading.value = false
            _weatherList.value = emptyList()
            handler.postDelayed(fetchRunnable, FETCH_INTERVAL)
            return
        }

        val results = arrayOfNulls<Weather>(cityList.size)
        val responseCount = AtomicInteger(0)
        val total = cityList.size

        for ((index, city) in cityList.withIndex()) {
            repository.retrieveForecast(city, object : WeatherCallback {
                override fun onSuccess(weather: Weather) {
                    results[index] = weather
                    if (responseCount.incrementAndGet() == total) {
                        _weatherList.postValue(results.filterNotNull())
                        _isLoading.postValue(false)
                        handler.postDelayed(fetchRunnable, FETCH_INTERVAL)
                    }
                }

                override fun onFailure(msg: String) {
                    if (responseCount.incrementAndGet() == total) {
                        val list = results.filterNotNull()
                        if (list.isNotEmpty()) {
                            _weatherList.postValue(list)
                        }
                        _isLoading.postValue(false)
                        handler.postDelayed(fetchRunnable, FETCH_INTERVAL)
                    }
                }
            })
        }
    }

    fun searchCity(query: String) {
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            return
        }
        _isSearching.value = true
        repository.searchCity(query,
            onResult = {
                _searchResults.postValue(it)
                _isSearching.postValue(false)
            },
            onError = {
                _searchResults.postValue(emptyList())
                _isSearching.postValue(false)
            }
        )
    }

    fun addCity(geoResult: GeoResult) {
        val city = City(geoResult.name, geoResult.lat, geoResult.lon)
        repository.addCity(city)
        _cities.value = repository.getCities()
        _searchResults.value = emptyList()
        fetchAllForecasts()
    }

    fun removeCity(city: City) {
        repository.removeCity(city)
        _cities.value = repository.getCities()
        fetchAllForecasts()
    }

    fun loadForecast(index: Int) {
        val cityList = repository.getCities()
        val city = cityList.getOrNull(index) ?: return
        repository.retrieveHourlyForecast(city,
            onResult = { _forecastItems.postValue(it.list) },
            onError = { _forecastItems.postValue(emptyList()) }
        )
    }

    override fun onCleared() {
        handler.removeCallbacks(fetchRunnable)
        super.onCleared()
    }

    companion object {
        private const val FETCH_INTERVAL = 120_000L
    }
}