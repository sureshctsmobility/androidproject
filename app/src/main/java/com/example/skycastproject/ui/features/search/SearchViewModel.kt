package com.example.skycastproject.ui.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycastproject.data.local.dao.TrackedCityDao
import com.example.skycastproject.data.local.entity.TrackedCityEntity
import com.example.skycastproject.data.remote.api.GeocodingApi
import com.example.skycastproject.data.remote.dto.RemoteCityMetadataDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val geocodingApi: GeocodingApi,
    private val trackedCityDao: TrackedCityDao
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _suggestions = MutableStateFlow<List<RemoteCityMetadataDto>>(emptyList())
    val suggestions = _suggestions.asStateFlow()

    init {
        viewModelScope.launch {
            _query.debounce(500).filter { it.trim().length >= 2 }.distinctUntilChanged().collect { text ->
                _isLoading.value = true
                try {
                    val res = geocodingApi.searchCities(text)
                    _suggestions.value = res.results ?: emptyList()
                } catch(e: Exception) { _suggestions.value = emptyList() }
                finally { _isLoading.value = false }
            }
        }
    }

    fun onQueryChange(text: String) {
        _query.value = text
        if (text.trim().length < 2) _suggestions.value = emptyList()
    }

    fun saveLocation(meta: RemoteCityMetadataDto) {
        viewModelScope.launch {
            trackedCityDao.insertTrackedCity(TrackedCityEntity(meta.name, meta.latitude, meta.longitude, meta.country, System.currentTimeMillis()))
        }
    }
}