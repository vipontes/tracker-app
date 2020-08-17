package br.net.easify.tracker.repositories.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "token")
data class DbToken(
    @PrimaryKey
    var token: String,
    var refresh_token: String
)