package br.net.easify.tracker.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_route")
data class DbRoute(

    @PrimaryKey
    val user_route_id: Int,
    val user_id: Int,
    var user_route_description: String,
    var user_route_start_time: String,
    var user_route_end_time: String?
)