package br.net.easify.tracker.repositories.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_route")
data class DbRoute(

    @PrimaryKey(autoGenerate = true)
    var user_route_id: Long?,
    var user_id: Long,
    var user_route_duration: String,
    var user_route_distance: String,
    var user_route_calories: String,
    var user_route_rhythm: String,
    var user_route_speed: String,
    var user_route_description: String,
    var user_route_start_time: String,
    var user_route_end_time: String?,
    var in_progress: Int,
    var sync: Int
)