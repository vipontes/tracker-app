package br.net.easify.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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
}