package klodian.kambo.data.controllers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import arrow.core.Either
import com.google.android.gms.location.*
import klodian.kambo.data.di.IoDispatcher
import klodian.kambo.domain.repositories.LocationRepository
import kotlinx.coroutines.*
import javax.inject.Inject


class LocationRepositoryImpl @Inject constructor(
    private val context: Context,
    private val coroutineDispatcher: CoroutineDispatcher
) : LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    /** suppressed because useless, permissions are checked with [hasGeolocPermissions] method */
    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getLocation(): Either<LocationRepository.Error, LocationRepository.LocationData> =
        withContext(coroutineDispatcher) {
            suspendCancellableCoroutine { continuation ->

                if (!hasGeolocPermissions()) {
                    continuation.resume(
                        Either.left(LocationRepository.Error.PermissionsDenied),
                        onCancellation = null
                    )
                }

                // this can be configurable, or a policy
                val locationRequest = LocationRequest.create().apply {
                    interval = 5000 // Update interval in milliseconds
                    fastestInterval = 1000 // Fastest update interval in milliseconds
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Location accuracy priority
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            val lastLocation = locationResult.lastLocation
                            fusedLocationClient.removeLocationUpdates(this)
                            continuation.resume(
                                Either.right(
                                    LocationRepository.LocationData(
                                        latitude = lastLocation.latitude,
                                        longitude = lastLocation.longitude
                                    )
                                ), onCancellation = null
                            )
                        }
                    },
                    Looper.getMainLooper()
                )
            }
        }

    private fun hasGeolocPermissions(): Boolean =
        !(ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)

}