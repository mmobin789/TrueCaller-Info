package com.magicbio.truename.models

import com.google.gson.annotations.Expose

class InviteResponse(
    @Expose val status: Boolean,
    @Expose val msg: String?
)