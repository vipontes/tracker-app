package br.net.easify.tracker.helpers

import br.net.easify.tracker.database.model.DbRoutePath
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

class TrackerHelper {
    companion object {

        fun calculateAverageRhythmInMiliseconds(path: List<DbRoutePath>): Double {
            val averageSpeed = calculateAverageSpeedInKmPerHour(path)
            if (averageSpeed == 0.0) return 0.0
            return (1 / averageSpeed) * 60 * 60 * 1000
        }

        fun calculateAverageSpeedInKmPerHour(path: List<DbRoutePath>): Double {

            if (path.size < 2)
                return 0.0

            val totalDistance = calculateDistanceInMeters(path)

            val totalTime = calculateElapsedTimeInSeconds(
                path.first().user_route_path_datetime,
                path.last().user_route_path_datetime
            )

            val averageSpeedInMetersPerSeconds = totalDistance / totalTime

            return convertMetersPerSecondsIntoKilometerPerHour(averageSpeedInMetersPerSeconds)
        }

        fun calculateDistanceInMeters(path: List<DbRoutePath>): Double {

            if (path.size < 2)
                return 0.0


            var lastLatitude = 0.0
            var lastLongitude = 0.0
            var totalDistance = 0.0

            for (item in path) {
                val latitude = item.user_route_path_lat
                val longitude = item.user_route_path_lng

                if (lastLatitude != 0.0 && lastLongitude != 0.0) {
                    val distanceInMeters: Float = calculateDistanceBetweenTwoGeolocationsInMeters(
                        lastLatitude, lastLongitude, 0.0,
                        latitude, longitude, 0.0)
                    totalDistance += distanceInMeters
                }

                lastLatitude = latitude
                lastLongitude = longitude
            }

            return totalDistance
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
            elevation1: Double,
            latitude2: Double,
            longitude2: Double,
            elevation2: Double
        ): Float {
            val earthRadius = 6371

            val latDistance = Math.toRadians(latitude2 - latitude1)
            val lonDistance = Math.toRadians(longitude2 - longitude1)
            val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + (Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)))
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            var distance = earthRadius * c * 1000 // convert to meters


            val height: Double = elevation2 - elevation1

            distance = Math.pow(distance, 2.0) + Math.pow(height, 2.0)

            return sqrt(distance).toFloat()
        }
    }
}