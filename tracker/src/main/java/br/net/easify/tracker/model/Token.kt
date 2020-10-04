package br.net.easify.tracker.model

import com.google.gson.annotations.SerializedName

data class Token (

    @SerializedName("token")
    val token: String,

    @SerializedName("refresh_token")
    val refreshToken: String
)