package com.magicbio.truename.models

import com.google.gson.annotations.Expose

class AppUpdateResponse(
    @Expose val status: String,
    @Expose val url: String,
    @Expose val version: Long
) {
//    fun getEncodedUrl() = URLEncoder.encode(url, "UTF-8")
}