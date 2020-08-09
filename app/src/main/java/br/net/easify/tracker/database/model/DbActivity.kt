package br.net.easify.tracker.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity")
data class DbActivity(

    @PrimaryKey(autoGenerate = true)
    val activity_id: Long?,
    val user_route_id: Long,
    var duration: String,
    var distance: String,
    var calories: String,
    var rhythm: String,
    var speed: String,
    var in_progress: Int,
    var sync: Int,
    var started_at: String,
    var finished_at: String?
)