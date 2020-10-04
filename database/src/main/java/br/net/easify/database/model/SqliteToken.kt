package br.net.easify.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "token")
data class SqliteToken(
    @PrimaryKey
    var token: String,
    var refresh_token: String
) {
    constructor() : this("", "")
}