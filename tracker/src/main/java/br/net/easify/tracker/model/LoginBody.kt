package br.net.easify.tracker.model

import android.os.Parcel
import android.os.Parcelable

data class LoginBody (
    var user_email: String?,
    var user_password: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user_email)
        parcel.writeString(user_password)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoginBody> {
        override fun createFromParcel(parcel: Parcel): LoginBody {
            return LoginBody(parcel)
        }

        override fun newArray(size: Int): Array<LoginBody?> {
            return arrayOfNulls(size)
        }
    }
}