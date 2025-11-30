package com.example.foro_cinev1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foro_cinev1.data.api.models.Movie
import com.example.foro_cinev1.data.api.models.MovieDetail
import com.example.foro_cinev1.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel(
    private val repository: MovieRepository = MovieRepository()
) : ViewModel() {

    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies

    private val _nowPlayingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val nowPlayingMovies: StateFlow<List<Movie>> = _nowPlayingMovies

    private val _topRatedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val topRatedMovies: StateFlow<List<Movie>> = _topRatedMovies

    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults: StateFlow<List<Movie>> = _searchResults

    private val _movieDetail = MutableStateFlow<MovieDetail?>(null)
    val movieDetail: StateFlow<MovieDetail?> = _movieDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadPopularMovies()
    }

    fun loadPopularMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getPopularMovies()
                .onSuccess { movies ->
                    _popularMovies.value = movies
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Error al cargar películas"
                }

            _isLoading.value = false
        }
    }

    fun loadNowPlayingMovies() {
        viewModelScope.launch {
            _isLoading.value = true

            repository.getNowPlayingMovies()
                .onSuccess { movies ->
                    _nowPlayingMovies.value = movies
                }
                .onFailure { error ->
                    _errorMessage.value = error.message
                }

            _isLoading.value = false
        }
    }

    fun loadTopRatedMovies() {
        viewModelScope.launch {
            _isLoading.value = true

            repository.getTopRatedMovies()
                .onSuccess { movies ->
                    _topRatedMovies.value = movies
                }
                .onFailure { error ->
                    _errorMessage.value = error.message
                }

            _isLoading.value = false
        }
    }

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            repository.searchMovies(query)
                .onSuccess { movies ->
                    _searchResults.value = movies
                }
                .onFailure { error ->
                    _errorMessage.value = error.message
                }

            _isLoading.value = false
        }
    }

    fun loadMovieDetail(movieId: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.getMovieDetail(movieId)
                .onSuccess { detail ->
                    _movieDetail.value = detail
                }
                .onFailure { error ->
                    _errorMessage.value = error.message
                }

            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
