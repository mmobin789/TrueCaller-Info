package com.magicbio.truename.fragments.background

import android.os.AsyncTask
import com.magicbio.truename.models.CallLogModel

class FetchCallLog(private val onCallLogListener: OnCallLogListener) : AsyncTask<Unit, Unit, ArrayList<CallLogModel>>() {

    override fun doInBackground(vararg params: Unit?): ArrayList<CallLogModel> {
        return AppAsyncWorker.getCallLogs()
    }

    override fun onPostExecute(result: ArrayList<CallLogModel>?) {
        if (!result.isNullOrEmpty())
            onCallLogListener.onCallLog(result)
    }

    interface OnCallLogListener {
        fun onCallLog(result: ArrayList<CallLogModel>)
    }
}