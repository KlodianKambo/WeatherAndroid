package klodian.kambo.domain.usecases.location

import arrow.core.Either
import klodian.kambo.domain.repositories.LocationDataSource
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(private val locationData: LocationDataSource) {
    suspend operator fun invoke(): Either<LocationDataSource.Error, LocationDataSource.LocationData> {
        return locationData.getLocation()
    }
}