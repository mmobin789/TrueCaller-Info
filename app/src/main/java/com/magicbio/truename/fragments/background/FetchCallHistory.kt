package com.magicbio.truename.fragments.background

import android.os.AsyncTask
import com.magicbio.truename.models.CallLogModel

class FetchCallHistory(private val number: String, private val onCallHistoryListener: OnCallHistoryListener) : AsyncTask<Unit, Unit, ArrayList<CallLogModel>>() {

    override fun doInBackground(vararg params: Unit?): ArrayList<CallLogModel> {
        return AppAsyncWorker.getCallDetails(number)
    }

    override fun onPostExecute(result: ArrayList<CallLogModel>?) {
        if (!result.isNullOrEmpty())
            onCallHistoryListener.onCallHistory(result)
    }

    interface OnCallHistoryListener {
        fun onCallHistory(result: ArrayList<CallLogModel>)
    }
}