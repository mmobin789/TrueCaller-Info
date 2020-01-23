package com.magicbio.truename.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

object ContactUtils {
    @JvmStatic
    fun openWhatsAppChat(number: String, c: Context) {

        val whatsAppPackage = "com.whatsapp"

        if (!isAppInstalled(whatsAppPackage, c)) {
            Toast.makeText(c, "WhatsApp not Installed.", Toast.LENGTH_SHORT).show()
            val uri = Uri.parse("market://details?id=$whatsAppPackage")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            c.startActivity(goToMarket)
            return
        }


        try {
            val numberWithCountryCodeNoPlus = number.replace(" ", "").removePrefix("+")
            var fixedNumber = numberWithCountryCodeNoPlus
            if (!numberWithCountryCodeNoPlus.startsWith("92"))
                fixedNumber = "92${numberWithCountryCodeNoPlus.substring(1)}"
            val sendIntent = Intent("$whatsAppPackage.Conversation")
            //  sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
            sendIntent.putExtra("jid", "$fixedNumber@s.whatsapp.net")
            c.startActivity(sendIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun isAppInstalled(packageName: String, c: Context): Boolean {
        val pm = c.packageManager
        var app_installed = false
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            app_installed = true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return app_installed
    }
}