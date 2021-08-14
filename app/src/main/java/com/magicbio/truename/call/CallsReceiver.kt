package com.magicbio.truename.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

class CallsReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val callsListener = CallsListener12AndAbove(context)
                telephonyManager.registerTelephonyCallback(context.mainExecutor, callsListener)
            } else {
                telephonyManager.listen(
                    CallsListener(context),
                    PhoneStateListener.LISTEN_CALL_STATE
                )
            }

        }

    }
}


