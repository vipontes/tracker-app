package br.net.easify.tracker.repositories.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.net.easify.tracker.model.Route

@Entity(tableName = "user_route")
data class SqliteRoute(
    @PrimaryKey(autoGenerate = true)
    var user_route_id: Long?,
    var user_id: Long,
    var user_route_duration: String?,
    var user_route_distance: String?,
    var user_route_calories: String?,
    var user_route_rhythm: String?,
    var user_route_speed: String?,
    var user_route_description: String?,
    var user_route_start_time: String?,
    var user_route_end_time: String?,
    var in_progress: Int,
    var sync: Int
) {
    constructor() : this(
        0,
        0,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        0,
        0
    )

    constructor(inProgress: Int, sync: Int) : this(
        0,
        0,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        inProgress,
        sync
    )

    fun toRoute(): Route {

        var userRouteId: Long = 0;
        this.user_route_id?.let {
            userRouteId = it
        }

        return Route(
            userRouteId,
            this.user_id,
            this.user_route_duration,
            this.user_route_distance,
            this.user_route_calories,
            this.user_route_rhythm,
            this.user_route_speed,
            this.user_route_description,
            this.user_route_start_time,
            this.user_route_end_time
        )
    }

    fun fromRoute(route: Route): SqliteRoute {
        this.user_route_id = route.userRouteId
        this.user_id = route.userId
        this.user_route_duration = route.userRouteDuration
        this.user_route_distance = route.userRouteDistance
        this.user_route_calories = route.userRouteCalories
        this.user_route_rhythm = route.userRouteRhythm
        this.user_route_speed = route.userRouteSpeed
        this.user_route_description = route.userRouteDescription
        this.user_route_start_time = route.userRouteStartTime
        this.user_route_end_time = route.userRouteEndTime

        return this
    }
}