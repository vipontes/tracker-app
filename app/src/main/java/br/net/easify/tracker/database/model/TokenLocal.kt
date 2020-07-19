package br.net.easify.tracker.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "token")
data class TokenLocal(
    @PrimaryKey
    var token: String,
    var refresh_token: String
)