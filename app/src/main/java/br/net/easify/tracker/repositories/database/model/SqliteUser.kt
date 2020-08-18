package br.net.easify.tracker.repositories.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.net.easify.tracker.model.User

@Entity(tableName = "user")
data class SqliteUser(

    @PrimaryKey
    var user_Id: Long?,
    var user_name: String?,
    var user_email: String?,
    var user_password: String?,
    var user_avatar: String?,
    var user_active: Int,
    var user_created_at: String?,
    var user_weight: Int,
    var token: String?,
    var refresh_token: String?
) {
    constructor() : this(
        0,
        "",
        "",
        "",
        "",
        0,
        "",
        0,
        "",
        ""
    )

    fun fromUser(user: User): SqliteUser {
        this.user_Id = user.userId
        this.user_name = user.userName
        this.user_email = user.userEmail
        this.user_password = user.userPassword
        this.user_avatar = user.userAvatar
        this.user_active = user.userActive
        this.user_created_at = user.userCreatedAt
        this.user_weight = user.userWeight
        this.token = user.token
        this.refresh_token = user.refreshToken

        return this
    }
}