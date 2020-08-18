package br.net.easify.tracker.repositories.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.net.easify.tracker.model.RoutePath

@Entity(tableName = "user_route_path", indices = [Index(value = ["user_route_path_datetime"], unique = true)])
data class SqliteRoutePath(

    @PrimaryKey(autoGenerate = true)
    var user_route_path_id: Long?,
    var user_route_id: Long,
    var user_route_path_lat: Double,
    var user_route_path_lng: Double,
    var user_route_path_altitude: Double,
    var user_route_path_datetime: String?
) {
    constructor() : this(
        0,
        0,
        0.0,
        0.0,
        0.0,
        ""
    )

    fun toRoutePath(): RoutePath {
        var userRoutePathId: Long = 0;
        this.user_route_path_id?.let {
            userRoutePathId = it
        }

        return RoutePath(
            userRoutePathId,
            this.user_route_id,
            this.user_route_path_lat,
            this.user_route_path_lng,
            this.user_route_path_altitude,
            this.user_route_path_datetime
        )
    }

    fun fromRoutePath(routePath: RoutePath): SqliteRoutePath {
        this.user_route_path_id = routePath.userRoutePathId
        this.user_route_id = routePath.userRouteId
        this.user_route_path_lat = routePath.userRoutePathLat
        this.user_route_path_lng = routePath.userRoutePathLng
        this.user_route_path_altitude = routePath.userRoutePathAltitude
        this.user_route_path_datetime = routePath.userRoutePathDatetime

        return this
    }
}