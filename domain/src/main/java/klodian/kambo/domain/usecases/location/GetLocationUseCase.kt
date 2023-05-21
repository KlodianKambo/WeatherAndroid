package klodian.kambo.domain.usecases.location

import arrow.core.Either
import klodian.kambo.domain.repositories.LocationRepository
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(private val locationData: LocationRepository) {
    suspend operator fun invoke(): Either<LocationRepository.Error, LocationRepository.LocationData> {
        return locationData.getLocation()
    }
}