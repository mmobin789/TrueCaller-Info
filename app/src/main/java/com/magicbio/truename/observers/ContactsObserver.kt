package com.magicbio.truename.observers

import android.content.Context
import android.database.ContentObserver
import com.magicbio.truename.TrueName
import com.magicbio.truename.fragments.background.AppAsyncWorker


class ContactsObserver(private val context: Context) : ContentObserver(null) {

    override fun onChange(selfChange: Boolean) {
        AppAsyncWorker.getLastContact(TrueName.getUserId(context))
    }
}