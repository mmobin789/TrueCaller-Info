package com.magicbio.truename.models

import com.google.gson.annotations.SerializedName
import com.magicbio.truename.db.contacts.Contact

class UploadContactsRequest(
    val data: ArrayList<Contact>,
    @SerializedName("user_id") val uid: Int
)