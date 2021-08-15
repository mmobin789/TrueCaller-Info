package com.magicbio.truename.call

import android.content.Context
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log

class CallScreenService : CallScreeningService() {

    private val telephonyManager by lazy {
        getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }


    private lateinit var callsListener12AndAbove: CallsListener12AndAbove

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle.toString().split(":")[1]
        Log.d("CallScreen", phoneNumber)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            callsListener12AndAbove = CallsListener12AndAbove(this, phoneNumber)
            telephonyManager.registerTelephonyCallback(mainExecutor, callsListener12AndAbove)
        } else {
            telephonyManager.listen(
                CallsListener(this),
                PhoneStateListener.LISTEN_CALL_STATE
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager.unregisterTelephonyCallback(callsListener12AndAbove)
        }

    }
}