package br.net.easify.tracker.model

import android.os.Parcel
import android.os.Parcelable

data class User(
    var userId: Long?,
    var userName: String?,
    var userEmail: String?,
    var userPassword: String?,
    var userActive: Int,
    var userAvatar: String?,
    var userCreatedAt: String?,
    var userWeight: Int,
    var token: String?,
    var refreshToken: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(userId)
        parcel.writeString(userName)
        parcel.writeString(userEmail)
        parcel.writeString(userPassword)
        parcel.writeInt(userActive)
        parcel.writeString(userAvatar)
        parcel.writeString(userCreatedAt)
        parcel.writeInt(userWeight)
        parcel.writeString(token)
        parcel.writeString(refreshToken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}