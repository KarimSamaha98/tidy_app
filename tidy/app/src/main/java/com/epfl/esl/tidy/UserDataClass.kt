package com.epfl.esl.tidy

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserDataClass (var email: String, var password: String, var first_name: String, var last_name: String,   var key: String, var space_id: String = "", var admin: String = "", var image: Uri? = Uri.parse("android.resource://com.epfl.esl.tidy/" + R.drawable.user)): Parcelable