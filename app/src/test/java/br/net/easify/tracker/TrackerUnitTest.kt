package br.net.easify.tracker

import br.net.easify.tracker.database.model.DbRoutePath
import br.net.easify.tracker.helpers.TrackerHelper
import org.junit.Test

import org.junit.Assert.*


class TrackerUnitTest {

    @Test
    fun testSpeedCalculation() {

        val path = arrayListOf<DbRoutePath>()

        path.add(
            DbRoutePath(
                null,
                1,
                -25.4813728,
                -49.2325226,
                0.0,
                "2020-08-08 10:26:30")
            )

        var speed = TrackerHelper.calculateAverageSpeedInKmPerHour(path)
        assertEquals(0.0, speed, 0.001 )

        path.add(
            DbRoutePath(
                null,
                1,
                -25.4813764,
                -49.2325567,
                0.0,
                "2020-08-08 10:26:49")
        )

        speed = TrackerHelper.calculateAverageSpeedInKmPerHour(path)
        assert(speed > 0)
    }


    @Test
    fun testRhythmCalculation() {

        val path = arrayListOf<DbRoutePath>()

        path.add(
            DbRoutePath(
                null,
                1,
                -25.4813728,
                -49.2325226,
                0.0,
                "2020-08-08 10:26:30")
        )

        var rhythm = TrackerHelper.calculateAverageRhythmInMilisecondsPerKilometer(path)
        assertEquals(0.0, rhythm, 0.001 )

        path.add(
            DbRoutePath(
                null,
                1,
                -25.4813764,
                -49.2325567,
                0.0,
                "2020-08-08 10:26:49")
        )

        rhythm = TrackerHelper.calculateAverageRhythmInMilisecondsPerKilometer(path)
        assert(rhythm > 0)
    }

    @Test
    fun testConversionFromKmPerHourToMinutesPerKm() {
        val rhythm = TrackerHelper.calculateRhythmInMinutesPerKm(10.0)
        assertEquals(6.0, rhythm, 0.001)
    }
}