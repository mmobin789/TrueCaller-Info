package com.magicbio.truename.fragments.background

import android.content.ContentResolver
import android.content.Context
import android.database.DatabaseUtils
import android.os.AsyncTask
import android.provider.Telephony
import com.magicbio.truename.models.Sms

class FetchAllSms : AsyncTask<Context, Unit, ArrayList<Sms>>() {


    var onComplete: ((ArrayList<Sms>) -> Unit)? = null

    override fun doInBackground(vararg params: Context?): ArrayList<Sms> {
        return getAllSms(params[0]!!)
    }

    override fun onPostExecute(result: ArrayList<Sms>) {
        onComplete?.invoke(result)
    }

    private fun getAllSms(context: Context): ArrayList<Sms> {

        var objSms: Sms
        // Uri message = Uri.parse("content://sms/conversations");
        val cr: ContentResolver = context.contentResolver
        //Telephony.Sms.PERSON;
        val projection = arrayOf("thread_id", "MAX(date) as date", "COUNT(*) AS msg_count", "body", "address", "(COUNT(*)-SUM(read)) as unread")
        val c = cr.query(Telephony.Sms.CONTENT_URI, projection, "thread_id) GROUP BY (thread_id", null, null)
        DatabaseUtils.dumpCursor(c)
        val totalSMS = c!!.count
        val lstSms = ArrayList<Sms>(totalSMS)
        try {
            if (c.moveToFirst()) {
                for (i in 0 until totalSMS) {
                    objSms = Sms()
                    objSms.id = c.getString(c.getColumnIndexOrThrow("thread_id"))
                    objSms.address = c.getString(c
                            .getColumnIndexOrThrow("address"))
                    objSms.msg = c.getString(c.getColumnIndexOrThrow("body"))
                    objSms.readState = c.getString(c.getColumnIndex("unread"))
                    objSms.time = c.getString(c.getColumnIndexOrThrow("date"))
                    if (c.getString(c.getColumnIndexOrThrow("msg_count")).contains("1")) {
                        objSms.folderName = "inbox"
                    } else {
                        objSms.folderName = "sent"
                    }
                    lstSms.add(objSms)
                    c.moveToNext()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // else {
// throw new RuntimeException("You have no SMS");
// }
        c.close()
        return lstSms
    }
}