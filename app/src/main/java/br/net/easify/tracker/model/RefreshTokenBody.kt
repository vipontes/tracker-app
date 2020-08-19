package br.net.easify.tracker.model

import android.os.Parcel
import android.os.Parcelable

data class RefreshTokenBody (
    val refresh_token: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(refresh_token)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RefreshTokenBody> {
        override fun createFromParcel(parcel: Parcel): RefreshTokenBody {
            return RefreshTokenBody(parcel)
        }

        override fun newArray(size: Int): Array<RefreshTokenBody?> {
            return arrayOfNulls(size)
        }
    }
}