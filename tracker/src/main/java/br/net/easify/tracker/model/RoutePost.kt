package br.net.easify.tracker.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RoutePost (

    @SerializedName("user_id")
    var userId: Long,

    @SerializedName("user_route_duration")
    var userRouteDuration: String?,

    @SerializedName("user_route_distance")
    var userRouteDistance: String?,

    @SerializedName("user_route_calories")
    var userRouteCalories: String?,

    @SerializedName("user_route_rhythm")
    var userRouteRhythm: String?,

    @SerializedName("user_route_speed")
    var userRouteSpeed: String?,

    @SerializedName("user_route_description")
    var userRouteDescription: String?,

    @SerializedName("user_route_start_time")
    var userRouteStartTime: String?,

    @SerializedName("user_route_end_time")
    var userRouteEndTime: String?,

    @SerializedName("path")
    val path: List<RoutePathPost>?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(RoutePathPost)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(userId)
        parcel.writeString(userRouteDuration)
        parcel.writeString(userRouteDistance)
        parcel.writeString(userRouteCalories)
        parcel.writeString(userRouteRhythm)
        parcel.writeString(userRouteSpeed)
        parcel.writeString(userRouteDescription)
        parcel.writeString(userRouteStartTime)
        parcel.writeString(userRouteEndTime)
        parcel.writeTypedList(path)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoutePost> {
        override fun createFromParcel(parcel: Parcel): RoutePost {
            return RoutePost(parcel)
        }

        override fun newArray(size: Int): Array<RoutePost?> {
            return arrayOfNulls(size)
        }
    }
}