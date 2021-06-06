package com.magicbio.truename.observers

import android.database.ContentObserver
import com.magicbio.truename.fragments.background.AppAsyncWorker


class CallLogsObserver : ContentObserver(null) {

    override fun onChange(selfChange: Boolean) {
        AppAsyncWorker.getLastCallLog()
    }
}