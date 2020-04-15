package com.magicbio.truename.fragments.background

import android.os.AsyncTask
import com.magicbio.truename.models.Sms

class FetchMessages(private val onMessagesListener: OnMessagesListener) : AsyncTask<Unit, Unit, ArrayList<Sms>>() {

    override fun doInBackground(vararg params: Unit?): ArrayList<Sms> {

        return AppAsyncWorker.getAllSms()
    }

    override fun onPostExecute(result: ArrayList<Sms>?) {
        if (!result.isNullOrEmpty())
            onMessagesListener.onMessages(result)


    }

    interface OnMessagesListener {
        fun onMessages(result: ArrayList<Sms>)
    }
}