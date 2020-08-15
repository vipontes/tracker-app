package br.net.easify.tracker.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RoutePost (

    @SerializedName("user_id")
    var userId: Int,

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
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(RoutePathPost)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
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