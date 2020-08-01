package br.net.easify.tracker.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_route_path")
data class DbRoutePath(

    @PrimaryKey
    val user_route_path_id: Int,
    val user_route_id: Int,
    var user_route_path_lat: Double,
    var user_route_path_lng: Double,
    var user_route_path_datetime: String
)