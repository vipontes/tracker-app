package br.net.easify.tracker.model

import android.os.Parcel
import android.os.Parcelable

data class Route (
    var userRouteId: Long,
    var userId: Long,
    var userRouteDuration: String?,
    var userRouteDistance: String?,
    var userRouteCalories: String?,
    var userRouteRhythm: String?,
    var userRouteSpeed: String?,
    var userRouteDescription: String?,
    var userRouteStartTime: String?,
    var userRouteEndTime: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(userRouteId)
        parcel.writeLong(userId)
        parcel.writeString(userRouteDuration)
        parcel.writeString(userRouteDistance)
        parcel.writeString(userRouteCalories)
        parcel.writeString(userRouteRhythm)
        parcel.writeString(userRouteSpeed)
        parcel.writeString(userRouteDescription)
        parcel.writeString(userRouteStartTime)
        parcel.writeString(userRouteEndTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Route> {
        override fun createFromParcel(parcel: Parcel): Route {
            return Route(parcel)
        }

        override fun newArray(size: Int): Array<Route?> {
            return arrayOfNulls(size)
        }
    }
}