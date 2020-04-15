package com.magicbio.truename.fragments.background

import android.os.AsyncTask
import com.magicbio.truename.activeandroid.Contact

class FetchContacts(private val onContactsListener: OnContactsListener) : AsyncTask<Unit, Unit, ArrayList<Contact>>() {
    override fun doInBackground(vararg params: Unit?): ArrayList<Contact> {
        return AppAsyncWorker.getContacts()
    }

    override fun onPostExecute(result: ArrayList<Contact>?) {
        if (!result.isNullOrEmpty())
            onContactsListener.onContacts(result)
    }


    interface OnContactsListener {
        fun onContacts(result: ArrayList<Contact>)
    }
}