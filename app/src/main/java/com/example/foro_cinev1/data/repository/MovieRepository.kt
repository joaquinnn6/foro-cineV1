package com.example.foro_cinev1.data.repository

import com.example.foro_cinev1.data.api.RetrofitClient
import com.example.foro_cinev1.data.api.models.Movie
import com.example.foro_cinev1.data.api.models.MovieDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository {

    private val apiService = RetrofitClient.apiService
    private val apiKey = RetrofitClient.API_KEY

    suspend fun getPopularMovies(): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPopularMovies(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.results)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNowPlayingMovies(): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getNowPlayingMovies(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.results)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopRatedMovies(): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTopRatedMovies(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.results)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchMovies(query: String): Result<List<Movie>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchMovies(apiKey, query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.results)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMovieDetail(movieId, apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}