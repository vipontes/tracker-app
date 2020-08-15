package br.net.easify.tracker.model

import android.os.Parcel
import android.os.Parcelable

data class RoutePath (
    var userRoutePathId: Int,
    var userRouteId: Int,
    var userRoutePathLat: Double,
    var userRoutePathLng: Double,
    var userRoutePathAltitude: Double,
    var userRoutePathDatetime: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userRoutePathId)
        parcel.writeInt(userRouteId)
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