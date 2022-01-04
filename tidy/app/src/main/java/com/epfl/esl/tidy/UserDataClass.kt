package com.epfl.esl.tidy

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserDataClass (val email: String, val password: String, val first_name: String, val last_name: String,   val key: String, val space_id: String = ""): Parcelable