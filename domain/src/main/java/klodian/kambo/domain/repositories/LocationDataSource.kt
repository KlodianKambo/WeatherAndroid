package klodian.kambo.domain.repositories

import arrow.core.Either

interface LocationDataSource {
    data class LocationData(
        val latitude: Double,
        val longitude: Double
    )

    sealed class Error {
        object Generic : Error()
        object PermissionsDenied : Error()
    }

    suspend fun getLocation(): Either<Error, LocationData>
}