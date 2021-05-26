package com.magicbio.truename.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.magicbio.truename.db.contacts.Contact

class UploadContactsRequest(
    @Expose
    @SerializedName("data")
    val contacts: ArrayList<Contact>,
    @Expose
    @SerializedName("user_id") val uid: Int
)