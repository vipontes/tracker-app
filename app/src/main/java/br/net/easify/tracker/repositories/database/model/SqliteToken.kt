package br.net.easify.tracker.repositories.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.model.User

@Entity(tableName = "token")
data class SqliteToken(
    @PrimaryKey
    var token: String,
    var refresh_token: String
) {
    constructor() : this("", "")

    fun fromToken(token: Token): SqliteToken {
        this.token = token.token
        this.refresh_token = token.refreshToken

        return this
    }
}