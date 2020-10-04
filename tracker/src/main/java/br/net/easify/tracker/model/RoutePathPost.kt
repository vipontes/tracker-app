package br.net.easify.tracker.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RoutePathPost (
    @SerializedName("user_route_path_latitude")
    val userRoutePathLatitude: Double,

    @SerializedName("user_route_path_longitude")
    val userRoutePathLongitude: Double,

    @SerializedName("user_route_path_altitude")
    val userRoutePathAltitude: Double,

    @SerializedName("user_route_path_datetime")
    val userRoutePathDatetime: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(userRoutePathLatitude)
        parcel.writeDouble(userRoutePathLongitude)
        parcel.writeDouble(userRoutePathAltitude)
        parcel.writeString(userRoutePathDatetime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoutePathPost> {
        override fun createFromParcel(parcel: Parcel): RoutePathPost {
            return RoutePathPost(parcel)
        }

        override fun newArray(size: Int): Array<RoutePathPost?> {
            return arrayOfNulls(size)
        }
    }
}