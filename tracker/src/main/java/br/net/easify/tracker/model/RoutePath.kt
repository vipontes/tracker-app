package br.net.easify.tracker.model

import android.os.Parcel
import android.os.Parcelable

data class RoutePath (
    var userRoutePathId: Long,
    var userRouteId: Long,
    var userRoutePathLat: Double,
    var userRoutePathLng: Double,
    var userRoutePathAltitude: Double,
    var userRoutePathDatetime: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(userRoutePathId)
        parcel.writeLong(userRouteId)
        parcel.writeDouble(userRoutePathLat)
        parcel.writeDouble(userRoutePathLng)
        parcel.writeDouble(userRoutePathAltitude)
        parcel.writeString(userRoutePathDatetime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoutePath> {
        override fun createFromParcel(parcel: Parcel): RoutePath {
            return RoutePath(parcel)
        }

        override fun newArray(size: Int): Array<RoutePath?> {
            return arrayOfNulls(size)
        }
    }
}