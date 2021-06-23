package com.magicbio.truename.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class InviteResponse(
    @Expose val status: Boolean,
    @Expose val msg: String?,
    @SerializedName("typea")
    @Expose val type: String?
)