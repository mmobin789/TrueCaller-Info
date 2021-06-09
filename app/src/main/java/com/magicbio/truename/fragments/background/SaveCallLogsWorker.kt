package com.magicbio.truename.fragments.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.magicbio.truename.R
import com.magicbio.truename.TrueName

class SaveCallLogsWorker(private val context: Context, workerParameters: WorkerParameters) :
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
            .setContentText("Updating Call logs")
            .setSmallIcon(R.drawable.app_icon)
            .setAutoCancel(false)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .build()
        setForeground(ForegroundInfo(4, notification))
        AppAsyncWorker.saveCallLog()
        setProgress(Data.EMPTY)
        nm.cancel(4)
        TrueName.saveLastUpdateTime(context)
        return Result.success()
    }


}