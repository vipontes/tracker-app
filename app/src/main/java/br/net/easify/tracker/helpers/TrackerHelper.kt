package br.net.easify.tracker.helpers

import android.location.Location
import br.net.easify.tracker.database.model.DbRoutePath
import java.text.SimpleDateFormat
import java.util.*

class TrackerHelper {
    companion object {

        fun calculateAverageRhythmInMinPerKm(path: List<DbRoutePath>): Double {
            val averageSpeed = calculateAverageSpeedInKmPerHour(path)

            if (averageSpeed == 0.0) return 0.0

            return (1 / averageSpeed) * 60
        }

        fun calculateAverageSpeedInKmPerHour(path: List<DbRoutePath>): Double {

            if (path.size < 2) {
                return 0.0
            }

            var lastLatitude = 0.0
            var lastLongitude = 0.0
            var totalDistance = 0.0
            var totalTime = calculateElapsedTimeInSeconds(
                path.first().user_route_path_datetime,
                path.last().user_route_path_datetime
            )

            for (item in path) {
                val latitude = item.user_route_path_lat
                val longitude = item.user_route_path_lng

                if (lastLatitude > 0.0 && lastLongitude > 0.0) {
                    val distanceInMeters: Float = calculateDistanceBetweenTwoGeolocationsInMeters(lastLatitude, lastLongitude, latitude, longitude)
                    totalDistance += distanceInMeters
                }

                lastLatitude = latitude
                lastLongitude = longitude
            }

            val averageSpeedInMetersPerSeconds = totalDistance / totalTime

            return convertMetersPerSecondsIntoKilometerPerHour(averageSpeedInMetersPerSeconds)
        }

        private fun calculateElapsedTimeInSeconds(startDate: String, endDate: String): Double {

            val startTime: Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate)
            val endTime: Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate)

            return (endTime.time - startTime.time) / 1000.0
        }

        private fun convertMetersPerSecondsIntoKilometerPerHour(value: Double): Double {
            return value * 3.6
        }

        private fun calculateDistanceBetweenTwoGeolocationsInMeters(
            latitude1: Double,
            longitude1: Double,
            latitude2: Double,
            longitude2: Double
        ): Float {
            val location1 = Location("")
            location1.latitude = latitude1
            location1.longitude = longitude1

            val location2 = Location("")
            location2.latitude = latitude2
            location2.longitude = longitude2

            return location1.distanceTo(location2)
        }
    }
}