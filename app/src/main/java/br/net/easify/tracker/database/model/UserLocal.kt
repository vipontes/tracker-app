package br.net.easify.tracker.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserLocal(

    @PrimaryKey
    val user_Id: Int,
    var user_name: String,
    var user_email: String,
    var user_password: String,
    var user_avatar: String,
    var user_active: Int,
    var user_created_at: String,
    var token: String,
    var refresh_token: String
)