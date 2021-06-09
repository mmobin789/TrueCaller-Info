package com.magicbio.truename.fragments.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.magicbio.truename.R

class SaveContactsWorker(private val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    private val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        // Create a Notification channel if necessary
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(
                    "background",
                    "Name Info",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )

            NotificationCompat.Builder(applicationContext, "background")
        } else NotificationCompat.Builder(applicationContext)

        val notification = builder
            .setContentTitle(context.getString(R.string.app_name))
            .setTicker(context.getString(R.string.app_name))
            .setContentText("Updating Contacts")
            .setSmallIcon(R.drawable.app_icon)
            .setAutoCancel(false)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .build()
        setForeground(ForegroundInfo(3, notification))
        AppAsyncWorker.saveContacts()
        setProgress(Data.EMPTY)
        nm.cancel(3)
        return Result.success()
    }


}