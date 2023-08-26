package com.example.polychat

import android.os.Parcel
import android.os.Parcelable


data class User(
    val userID: String,
    val stuNum: String,
    val stuName: String,
    val department: String,
    val email: String,
    val phone: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userID)
        parcel.writeString(stuNum)
        parcel.writeString(stuName)
        parcel.writeString(department)
        parcel.writeString(email)
        parcel.writeString(phone)
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
data class ChatMessage(val senderName: String, val messageText: String, val timestamp: Long)

data class Post(
    val postId: String = "",
    val title: String = "",
    val content: String = "",
    val stuName: String = "",
    val timestamp: Long = 0L
)
