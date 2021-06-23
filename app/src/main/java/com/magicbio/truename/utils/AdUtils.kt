package com.magicbio.truename.utils

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


object AdUtils {
    @JvmStatic
    fun loadBannerAd(adView: AdView) {
        adView.loadAd(AdRequest.Builder().build())
    }

   /* @SuppressLint("HardwareIds")
    @JvmStatic
    fun getDeviceId(context: Context): String {
        val androidId =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val testDeviceId = md5(androidId).toUpperCase(Locale.getDefault())
        Log.d("TestDeviceId", testDeviceId)
        return testDeviceId
    }*/


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