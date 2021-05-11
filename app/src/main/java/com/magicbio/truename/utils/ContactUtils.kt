package com.magicbio.truename.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.magicbio.truename.TrueName
import com.magicbio.truename.activeandroid.Contact
import com.magicbio.truename.activities.CallDetails
import com.magicbio.truename.fragments.background.AppAsyncWorker
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.geofencing.model.GeofenceModel
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider


object ContactUtils {
    private val context = TrueName.getInstance()

    @JvmStatic
    fun openCallDetailsActivity(contact: Contact?) {
        val intent =
            Intent(context, CallDetails::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("name", contact?.name)
        intent.putExtra("number", contact?.number)
        intent.putExtra("email", contact?.email)
        context.startActivity(intent)
    }

    @JvmStatic
    @SuppressLint("MissingPermission")
    fun callNumber(number: String) {
        val intent = Intent(
            Intent.ACTION_CALL,
            Uri.parse("tel:$number")
        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

    }

    @JvmStatic
    fun openDialer(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    @JvmStatic
    fun openSmsApp(number: String) {
        val uri = Uri.parse("smsto:$number")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("sms_body", "")
        context.startActivity(intent)
    }


    @JvmStatic
    fun makeWhatsAppCall(name: String?, videoCall: Boolean) {

        if (!isWhatsAppInstalled() || name.isNullOrBlank())
            return

        var type = "vnd.android.cursor.item/vnd.com.whatsapp.voip.call"
        if (videoCall)
            type = "vnd.android.cursor.item/vnd.com.whatsapp.video.call"

        AppAsyncWorker.getWhatsAppUserId(name, type) {
            if (it != -1L) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.setPackage("com.whatsapp")
                intent.setDataAndType(Uri.parse("content://com.android.contacts/data/$it"), type)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "$name does not uses WhatsApp", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun isWhatsAppInstalled(): Boolean {
        val whatsAppPackage = "com.whatsapp"
        if (!isAppInstalled(whatsAppPackage)) {
            Toast.makeText(context, "WhatsApp not Installed.", Toast.LENGTH_SHORT).show()
            val uri = Uri.parse("market://details?id=$whatsAppPackage")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(goToMarket)
            return false
        }

        return true
    }

    @JvmStatic
    fun openWhatsAppChat(number: String) {

        val whatsAppPackage = "com.whatsapp"

        if (!isWhatsAppInstalled()) {
            return
        }


        try {
            val numberWithCountryCodeNoPlus = number.replace(" ", "").removePrefix("+")
            var fixedNumber = numberWithCountryCodeNoPlus
            if (!numberWithCountryCodeNoPlus.startsWith("92"))
                fixedNumber = "92${numberWithCountryCodeNoPlus.substring(1)}"
            val sendIntent =
                Intent("$whatsAppPackage.Conversation").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //  sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
            sendIntent.putExtra("jid", "$fixedNumber@s.whatsapp.net")
            context.startActivity(sendIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun formatNumberToLocal(number: String?): String {

        if (number.isNullOrBlank()) {
            return "Unable to find this number in contacts"
        }

        if (!number.contains("92"))
            return number

        val numberFormatted = number.replace(" ", "").removePrefix("+92").removePrefix("92")
        return "0$numberFormatted"
    }

    private var provider: LocationGooglePlayServicesProvider? = null

    @JvmStatic
    fun shareLocationOnSms(phoneNumber: String?, name: String?) {
        try {
            startLocation()

            val lastLocation = SmartLocation.with(context).location().lastLocation

            lastLocation.apply {
                if (this == null) {
                    Log.e(javaClass.simpleName, "Failed 1st time try again")

                    val simpleCountDownTimer = SimpleCountDownTimer(
                        0,
                        1,
                        object : SimpleCountDownTimer.OnCountDownListener {
                            override fun onCountDownActive(time: String) {

                            }

                            override fun onCountDownFinished() {
                                shareLocationOnSms(phoneNumber, name)
                            }
                        })
                    simpleCountDownTimer.runOnBackgroundThread()

                    simpleCountDownTimer.start()


                } else
                    sendLocationSMS(phoneNumber, name, this)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startLocation() {

        provider = LocationGooglePlayServicesProvider()
        provider?.setCheckLocationSettings(true)
        val smartLocation = SmartLocation.Builder(context).logging(true).build()
        smartLocation.location(provider).start {

        }
        smartLocation.activity().start {
        }
        // Create some geofences
        val geoFence = GeofenceModel.Builder("1").setTransition(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setLatitude(39.47453120000001).setLongitude(-0.358065799999963).setRadius(500f).build()
        smartLocation.geofencing().add(geoFence).start { }
    }

    private fun sendLocationSMS(phoneNumber: String?, name: String?, currentLocation: Location) {
        val nameOrNumber = name ?: phoneNumber
        val smsManager = SmsManager.getDefault()
        val smsBody = StringBuffer()
        val uri =
            "http://maps.google.com/maps?q=" + currentLocation.latitude + "," + currentLocation.longitude
        smsBody.append(Uri.parse(uri))
        smsManager.sendTextMessage(phoneNumber, null, smsBody.toString(), null, null)
        Toast.makeText(
            context,
            "Your location is successfully shared with $nameOrNumber",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun isAppInstalled(packageName: String): Boolean {
        val pm = context.packageManager
        var appInstalled = false
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            appInstalled = true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return appInstalled
    }

    @JvmStatic
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        provider?.onActivityResult(requestCode, resultCode, data)
    }
}