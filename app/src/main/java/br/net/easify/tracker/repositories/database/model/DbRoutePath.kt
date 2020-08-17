package br.net.easify.tracker.repositories.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_route_path", indices=[Index(value = ["user_route_path_datetime"], unique = true)])
data class DbRoutePath(

    @PrimaryKey(autoGenerate = true)
    var user_route_path_id: Long?,
    var user_route_id: Long,
    var user_route_path_lat: Double,
    var user_route_path_lng: Double,
    var user_route_path_altitude: Double,
    var user_route_path_datetime: String
)