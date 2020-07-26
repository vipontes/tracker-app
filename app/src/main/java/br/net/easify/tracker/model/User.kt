package br.net.easify.tracker.model

data class User (
    var userId : Int,
    var userName : String,
    var userEmail : String,
    var userPassword : String,
    var userActive : Int,
    var userAvatar : String?,
    var userCreatedAt : String,
    var token : String,
    var refreshToken : String
)