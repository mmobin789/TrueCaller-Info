package com.magicbio.truename.fragments.background

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.provider.CallLog
import com.magicbio.truename.models.CallLogModel
import java.util.*

class FetchCallLogs : AsyncTask<Context, Unit, ArrayList<CallLogModel>>() {

    var onComplete: ((ArrayList<CallLogModel>) -> Unit)? = null

    override fun doInBackground(vararg params: Context?): ArrayList<CallLogModel> {
        return getCallDetails(params[0]!!)
    }

    override fun onPostExecute(result: ArrayList<CallLogModel>) {
        onComplete?.invoke(result)
    }

    private fun getCallDetails(context: Context): ArrayList<CallLogModel> {
        val sb = StringBuilder()

        val contacts = CallLog.Calls.CONTENT_URI
        @SuppressLint("MissingPermission") val managedCursor = context.contentResolver.query(contacts, null, null, null, "DATE desc")
        val callLogModelList = ArrayList<CallLogModel>(managedCursor!!.count)
        val number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
        val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
        val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
        val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
        val sim = managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID)
        val id = managedCursor.getColumnIndex(CallLog.Calls._ID)
        sb.append("Call Details :")
        while (managedCursor.moveToNext()) { //  HashMap rowDataCall = new HashMap<String, String>();
            val call = CallLogModel()
            val phNumber = managedCursor.getString(number)
            val callType = managedCursor.getString(type)
            val callDate = managedCursor.getString(date)
            val callDayTime = Date(java.lang.Long.valueOf(callDate)).toString()
            val name = managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
            val image = managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_ID))
            // long timestamp = convertDateToTimestamp(callDayTime);
            val callDuration = managedCursor.getString(duration)
            val simn = managedCursor.getString(sim)
            val sid = managedCursor.getString(id)
            val dircode = callType.toInt()
            val dir = when (dircode) {
                CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                CallLog.Calls.INCOMING_TYPE -> "INCOMING"
                CallLog.Calls.MISSED_TYPE -> "MISSED"
                else -> "OUTGOING"
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration
                    + " \nname :--- " + name)
            sb.append("\n----------------------------------")
            call.callType = dir
            call.callDate = callDate
            call.phNumber = phNumber
            call.callDayTime = callDayTime
            call.sim = simn
            call.name = name
            call._Id = sid
            call.image = image
            //  val hours = Integer.valueOf(callDuration) / 3600
            val minutes = Integer.valueOf(callDuration) % 3600 / 60
            val seconds = Integer.valueOf(callDuration) % 60
            call.callDuration = String.format("%02d:%02d", minutes, seconds)
            // Uri allCalls = Uri.parse("content://call_log/calls");
// Cursor c = ((MainActivity)getActivity()).managedQuery(allCalls, null, null, null, null);
//String id = c.getString(c.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
//Log.d("sim",id);
            callLogModelList.add(call)
        }
        managedCursor.close()
        //System.out.println(sb);
        return callLogModelList
    }


}