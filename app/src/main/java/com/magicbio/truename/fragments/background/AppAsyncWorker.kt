package com.magicbio.truename.fragments.background

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.database.DatabaseUtils
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.Telephony
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import com.activeandroid.ActiveAndroid
import com.magicbio.truename.TrueName
import com.magicbio.truename.activeandroid.Contact
import com.magicbio.truename.models.CallLogModel
import com.magicbio.truename.models.Sms
import com.magicbio.truename.utils.ContactUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


object AppAsyncWorker {

    private val context = TrueName.getInstance()
    private var smsList: ArrayList<Sms>? = null
    private var callLogList: ArrayList<CallLogModel>? = null
    /*  private var callLogList: ArrayList<CallLogModel>? = null
      private var contactsList: ArrayList<Contact>? = null*/

    @JvmStatic
    fun fetchCallHistory(number: String, onComplete: (ArrayList<CallLogModel>) -> Unit) {
        GlobalScope.launch {
            val callLogList = getCallDetails(number)
            withContext(Dispatchers.Main) {
                onComplete(callLogList)
            }
        }
    }

    @JvmStatic
    fun fetchAllMessages(onComplete: (ArrayList<Sms>) -> Unit, refresh: Boolean = false) {
        GlobalScope.launch {
            if (smsList.isNullOrEmpty() && !refresh)
                smsList = getAllSms()
            withContext(Dispatchers.Main) {
                onComplete(smsList!!)
            }
        }
    }

    @JvmStatic
    fun getContactByNumber(number: String, callback: (Contact?) -> Unit) {
        GlobalScope.launch {
            callback(getContacts().find { ContactUtils.formatNumberToLocal(it.getNumber()) == ContactUtils.formatNumberToLocal(number) })
        }
    }

    @JvmStatic
    fun getContactByName(name: String, callback: (Contact?) -> Unit) {
        GlobalScope.launch {
            callback(getContacts().find { it.name == name })
        }
    }

    @JvmStatic
    fun fetchContactsByNumber(number: String, onComplete: (ArrayList<Contact>) -> Unit) {
        GlobalScope.launch {
            val filtered = getContacts().filter {
                ContactUtils.formatNumberToLocal(it.number) == ContactUtils.formatNumberToLocal(number)
            }
            withContext(Dispatchers.Main)
            {
                onComplete(ArrayList(filtered))
            }
        }
    }

    @JvmStatic
    fun fetchCallLog(onComplete: ((ArrayList<CallLogModel>) -> Unit)?) {
        GlobalScope.launch {
            if (callLogList.isNullOrEmpty())
                callLogList = getCallDetails()
            withContext(Dispatchers.Main) {
                onComplete?.invoke(callLogList!!)
            }
        }
    }

    @JvmStatic
    fun fetchContacts(onComplete: (ArrayList<Contact>) -> Unit) {
        GlobalScope.launch {
            val contactsList = getContacts()
            withContext(Dispatchers.Main) {
                onComplete(contactsList)
            }
        }
    }


    fun getWhatsAppUserId(name: String, callType: String, callback: (Long) -> Unit) {
        GlobalScope.launch {
            val resolver = context.contentResolver
            val cursor = resolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    null, null, null,
                    ContactsContract.Contacts.DISPLAY_NAME)
            var found = false
            while (cursor?.moveToNext() == true) {
                val _id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID))
                val displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME))
                val mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE))
                found = displayName == name && mimeType == callType

                if (found) {
                    Log.d(this@AppAsyncWorker.javaClass.simpleName, "$_id $displayName $mimeType")
                    callback(_id)
                    break
                }
            }

            if (!found) {
                withContext(Dispatchers.Main)
                {
                    callback(-1)
                }
            }

            cursor?.close()
        }
    }

/*    private fun getContactList(): ArrayList<Contact> {
        var contactList: ArrayList<Contact>? = null

        *//* if (!Contact.getAll().isNullOrEmpty()) {
             contactList = Contact.getAll() as ArrayList<Contact>
             return contactList
         }*//*

        val cr = context.contentResolver
        val sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
        val cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, sortOrder)
        //DatabaseUtils.dumpCursor(cur)
        ActiveAndroid.beginTransaction()
        if (cur?.count ?: 0 > 0) {
            contactList = ArrayList(cur!!.count)
            while (cur.moveToNext()) {
                //  val contactModel = ContactModel()
                val contact = Contact()
                val id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))

                *//*   if (cur.getInt(cur.getColumnIndex(
                                   ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {*//*
                *//* val pCur = cr.query(
                         ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                         null,
                         ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), sortOrder)*//*


                val phoneNo = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val image = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI))

                //     contactModel.name = name
                //     contactModel.image = image
                //    contactModel.number = phoneNo
                try {
                    contact.setName(name)
                    contact.setNumber(phoneNo)
                    contact.email = getEmail(id, name)
                    contact.image = image
                    contact.save()
                    contactList.add(contact)

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }


                //pCur.close()

            }
        }
        cur?.close()
        ActiveAndroid.setTransactionSuccessful()
        ActiveAndroid.endTransaction()

        return contactList!!
    }*/

    private fun getContacts(): ArrayList<Contact> {
        if (!Contact.getAll().isNullOrEmpty()) {
            return Contact.getAll() as ArrayList<Contact>
        }
        ActiveAndroid.beginTransaction()
        val contacts: ArrayList<Contact> = ArrayList()
        val projection = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        )
        val filter = "${ContactsContract.Contacts.DISPLAY_NAME} NOT LIKE '%@%'"
        val order = String.format("%1\$s COLLATE NOCASE", ContactsContract.Contacts.DISPLAY_NAME)
        val cr = context.contentResolver
        val cursor: Cursor? = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, filter, null, order)
        if (cursor != null && cursor.moveToFirst()) {
            do { // get the contact's information
                val id: String = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name: String = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhone: Int = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                // get the user's email address
                var email: String? = null
                val ce: Cursor? = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", arrayOf(id), null)
                if (ce != null && ce.moveToFirst()) {
                    email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                    ce.close()
                }
                // get the user's phone number
                var phone: String? = null
                var image: String? = null
                if (hasPhone > 0) {
                    val cp: Cursor? = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)
                    if (cp != null && cp.moveToFirst()) {
                        phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        image = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI))

                        cp.close()
                    }
                }
                // if the user user has an email or phone then add it to contacts
                if ((!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                                && !email.equals(name, ignoreCase = true)) || !TextUtils.isEmpty(phone)) {
                    val contact = Contact()
                    contact.name = name
                    contact.email = email
                    contact.number = phone
                    contact.image = image
                    contact.contactId = id
                    contacts.add(contact)
                    val cid = contact.save()
                    Log.d("ContactID", cid.toString())
                }
            } while (cursor.moveToNext())
            // clean up cursor
            cursor.close()
        }
        ActiveAndroid.setTransactionSuccessful()
        ActiveAndroid.endTransaction()
        return contacts
    }


    private fun getCallDetails(): ArrayList<CallLogModel> {
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
            val callDayTime = Date(callDate.toLong()).toString()
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
            // call.email = getEmail()
            val hours = Integer.valueOf(callDuration) / 3600
            val minutes = Integer.valueOf(callDuration) % 3600 / 60
            val seconds = Integer.valueOf(callDuration) % 60
            call.callDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds)
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

    private fun getAllSms(): ArrayList<Sms> {

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

    private fun getCallDetails(numbers: String): ArrayList<CallLogModel> {
        val sb = StringBuffer()

        val contacts = CallLog.Calls.CONTENT_URI
        @SuppressLint("MissingPermission") val managedCursor = context.contentResolver.query(contacts, null, "number=?", arrayOf(numbers), "DATE desc")
        val number = managedCursor!!.getColumnIndex(CallLog.Calls.NUMBER)
        val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
        val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
        val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
        val sim = managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID)
        sb.append("Call Details :")
        val callLogModelList = ArrayList<CallLogModel>(managedCursor.count)
        while (managedCursor.moveToNext()) {
            val rowDataCall: HashMap<*, *> = HashMap<String, String>()
            val call = CallLogModel()
            val phNumber = managedCursor.getString(number)
            val callType = managedCursor.getString(type)
            val callDate = managedCursor.getString(date)
            val callDayTime = Date(java.lang.Long.valueOf(callDate)).toString()
            val name = managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
            // long timestamp = convertDateToTimestamp(callDayTime);
            val callDuration = managedCursor.getString(duration)
            val simn = managedCursor.getString(sim)
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
            val hours = Integer.valueOf(callDuration) / 3600
            val minutes = Integer.valueOf(callDuration) % 3600 / 60
            val seconds = Integer.valueOf(callDuration) % 60
            call.callDuration = String.format("%02d:%02d", minutes, seconds)
            // Uri allCalls = Uri.parse("content://call_log/calls");
// Cursor c = ((MainActivity)CallDetails.this).managedQuery(allCalls, null, null, null, null);
//String id = c.getString(c.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID));
//Log.d("sim",id);
            callLogModelList.add(call)
        }
        managedCursor.close()

        return callLogModelList
    }
}