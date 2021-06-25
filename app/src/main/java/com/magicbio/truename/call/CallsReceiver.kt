package com.magicbio.truename.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

class CallsReceiver : BroadcastReceiver() {

    private val incomingCallsListener = CallsListener()


    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.PHONE_STATE") {
            incomingCallsListener.setContext(context)
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephonyManager.listen(incomingCallsListener, PhoneStateListener.LISTEN_CALL_STATE)
        }

    }


}