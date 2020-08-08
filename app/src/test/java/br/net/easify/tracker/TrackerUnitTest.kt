package br.net.easify.tracker

import br.net.easify.tracker.database.model.DbRoutePath
import br.net.easify.tracker.helpers.TrackerHelper
import org.junit.Test

import org.junit.Assert.*



class TrackerUnitTest {

    @Test
    fun check_speed_calculation() {

        var path = arrayListOf<DbRoutePath>()

        path.add(
            DbRoutePath(
                null,
                1,
                -25.4813728,
                -49.2325226,
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
                "2020-08-08 10:26:49")
        )

        speed = TrackerHelper.calculateAverageSpeedInKmPerHour(path)
        assert(speed > 0)
    }

    @Test
    fun check_rhythm_calculation() {

        var path = arrayListOf<DbRoutePath>()

        path.add(
            DbRoutePath(
                null,
                1,
                -25.4813728,
                -49.2325226,
                "2020-08-08 10:26:30")
        )

        var rhythm = TrackerHelper.calculateAverageRhythmInMiliseconds(path)
        assertEquals(0.0, rhythm, 0.001 )

        path.add(
            DbRoutePath(
                null,
                1,
                -25.4813764,
                -49.2325567,
                "2020-08-08 10:26:49")
        )

        rhythm = TrackerHelper.calculateAverageRhythmInMiliseconds(path)
        assert(rhythm > 0)
    }
}