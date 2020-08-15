package br.net.easify.tracker.model

import android.os.Parcel
import android.os.Parcelable

data class Route (
    var userRouteId: Int,
    var userId: Int,
    var userRouteDescription: String?,
    var userRouteStartTime: String?,
    var userRouteEndTime: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userRouteId)
        parcel.writeInt(userId)
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