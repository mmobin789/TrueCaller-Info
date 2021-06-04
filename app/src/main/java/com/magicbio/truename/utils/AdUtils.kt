package com.magicbio.truename.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


object AdUtils {
    @JvmStatic
    fun loadBannerAd(adView: AdView) {
        adView.apply {

            loadAd(AdRequest.Builder().addTestDevice(getDeviceId(context)).build())
        }
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceId(context: Context): String {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val testDeviceId = md5(androidId).toUpperCase(Locale.getDefault())
        Log.d("TestDeviceId", testDeviceId)
        return testDeviceId
    }


    private fun md5(s: String): String {
        try { // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()
            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) hexString.append(Integer.toHexString(0xFF and messageDigest[i].toInt()))
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

}