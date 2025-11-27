package com.example.foro_cinev1.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class LocationHelper(private val context: Context) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Obtiene la ciudad o región actual del usuario usando Geocoder y la última ubicación conocida.
     * Prioriza localidad, subregión o región administrativa, devolviendo "Ubicación desconocida" si no hay datos.
     */
    @SuppressLint("MissingPermission")
    suspend fun obtenerCiudadActual(): String? = withContext(Dispatchers.IO) {
        try {
            // Espera la última ubicación conocida
            val location: Location? = fusedClient.lastLocation.await()

            location?.let {
                val geocoder = Geocoder(context, Locale.getDefault())
                val direcciones = geocoder.getFromLocation(it.latitude, it.longitude, 1)

                // Devuelve la mejor información posible
                val dir = direcciones?.firstOrNull()
                dir?.locality           // Ciudad
                    ?: dir?.subAdminArea // Comuna / Provincia
                    ?: dir?.adminArea    // Región
                    ?: "Ubicación desconocida"
            } ?: "Ubicación desconocida"
        } catch (e: Exception) {
            e.printStackTrace()
            "Ubicación desconocida"
        }
    }
}
