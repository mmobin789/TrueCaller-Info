package com.magicbio.truename.fragments.background

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.DatabaseUtils
import android.net.Uri
import android.os.Build
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.Telephony
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import com.magicbio.truename.TrueName
import com.magicbio.truename.activities.MainActivity
import com.magicbio.truename.adapters.CallLogsAdapter
import com.magicbio.truename.db.contacts.Contact
import com.magicbio.truename.models.*
import com.magicbio.truename.retrofit.ApiClient
import com.magicbio.truename.retrofit.ApiInterface
import com.magicbio.truename.utils.ContactUtils
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


object AppAsyncWorker {

    private val context = TrueName.getInstance()
    private val contactsDao = context.appDatabase.contactDao()
    private val callLogDao = context.appDatabase.callLogDao()
    private val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)


    fun saveCallLog() {
        if (!TrueName.areCallLogsSaved(context)) {
            val ids = callLogDao.addCallLog(getCallLogs())
            Log.d("AppAsyncWorker", "${ids.size} Call Logs added to DB")
            TrueName.setCallLogSaved(context)
        }
    }

    private fun getDayDiff(): Long {
        val lastUpdateTimeInMillis = TrueName.getLastUpdateTime(context)
        val calendar = Calendar.getInstance()
        return TimeUnit.DAYS.convert(
            calendar.timeInMillis - lastUpdateTimeInMillis,
            TimeUnit.MILLISECONDS
        ).also {
            Log.d("DayDiff", it.toString())
        }
    }

    @JvmStatic
    fun addNumberToSpam(number: String, onResponse: (String?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val status = apiInterface.addNumberToSpam(number).status
            withContext(Dispatchers.Main.immediate)
            {
                if (status)
                    onResponse(null)
                else onResponse(number)
            }
        }
    }

    @JvmStatic
    fun getNumberDetails(number: String, onSpam: () -> Unit) {
        apiInterface.getNumberDetails(number, "92").enqueue(object : Callback<GetNumberResponse> {
            override fun onResponse(
                call: Call<GetNumberResponse>,
                response: Response<GetNumberResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.run {
                        if (!data.spamCount.isNullOrBlank() && data.spamCount != "0")
                            onSpam()
                    }
                }
            }

            override fun onFailure(call: Call<GetNumberResponse>, t: Throwable) {
                t.printStackTrace()
            }
        }
        )
    }

    fun saveContacts() {
        if (!TrueName.areContactsUploaded(context)) {
            val uid = TrueName.getUserId(context)
            val contacts = getContacts()
            val ids = contactsDao.addContacts(contacts)
            Log.d("AppAsyncWorker", "${ids.size} Contacts added to DB")
            try {
                val success = apiInterface.uploadContacts(UploadContactsRequest(contacts, uid))
                    .execute().isSuccessful
                if (success) {
                    TrueName.setContactsUploaded(context)
                    val response = apiInterface.inviteSync("auto").execute()
                    response.body()?.also {
                        sendSMSToPhoneBook(contacts, it, smsCount = it.smsCount)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    @JvmStatic
    fun addCallLog(
        holder: CallLogsAdapter.MyViewHolder,
        callLogModel: CallLogModel,
        onSuccess: (CallLogModel, CallLogsAdapter.MyViewHolder) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
            val response = apiInterface.getNumberDetails(callLogModel.phNumber, "92").execute()
            if (response.body() != null && response.body()?.status == true) {
                val data = response.body()!!.data
                callLogModel.name = data.name
                callLogModel.numberByTrueName = true
                callLogDao.update(callLogModel)
                withContext(Dispatchers.Main.immediate) {
                    onSuccess(callLogModel, holder)
                }

            }
        }
    }


    private fun sendSMSToPhoneBook(
        contacts: List<Contact>,
        response: InviteResponse,
        dummyContacts: Boolean = false,
        smsCount: Int = 0
    ) {
        try {
            //  Log.d("InviteAPI", response.status.toString())
            // val body = JSONObject(response.string())
            val msg = response.msg
            if (!msg.isNullOrBlank() && response.type == "yes") {
                val phoneBook = if (dummyContacts) {
                    arrayListOf(Contact().apply {
                        name = "Test User 1"
                        numbers = arrayListOf("03101289585")
                    }, Contact().apply {
                        name = "Test User 2"
                        numbers = arrayListOf("03455555613")
                    })
                } else contacts

                val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    context.getSystemService(SmsManager::class.java)
                else SmsManager.getDefault()

                var pbCount = phoneBook.lastIndex

                if (smsCount <= pbCount) {
                    pbCount = smsCount
                }

                for (i in 0..pbCount) {
                    val contact = phoneBook[i]
                    contact.numbers.forEach { number ->
                        if (number.isPakistani()) {
                            val smsBody = StringBuffer()
                            smsBody.append(Uri.parse(msg))
                            smsManager.sendTextMessage(
                                number,
                                null,
                                smsBody.toString(),
                                null,
                                null
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun String.isPakistani(): Boolean {
        return startsWith("92") || startsWith("+92") || startsWith("03")
    }

    @JvmStatic
    fun loadContacts(
        offset: Int,
        onLoaded: (ArrayList<Contact>) -> Unit
    ) {
        val list =
            if (offset > 0) contactsDao.getContacts(offset) else contactsDao.getInitialContacts()
        onLoaded(list as ArrayList<Contact>)


    }


    @JvmStatic
    fun loadLastCallLogByNumber(number: String): CallLogModel? {
        return callLogDao.findLastCallLogByNumber(number)
    }

    @JvmStatic
    fun findContactByNumber(number: String): Contact? {
        return contactsDao.findContactByNumber(number)
    }


    @JvmStatic
    fun loadContactsBy(query: String): List<Contact> {
        return if (query.isBlank()) {
            contactsDao.getInitialContacts()
        } else {
            val number = query.toIntOrNull()
            if (number != null) {
                contactsDao.findContactsByNumbers(listOf("%$query%"))
            } else {
                contactsDao.findContactsByName("%$query%")
            }
        }

    }

    @JvmStatic
    fun loadCallLog(
        offset: Int,
        onLoaded: (ArrayList<CallLogModel>) -> Unit
    ) {
        val list =
            if (offset > 0) callLogDao.getCallLogs(offset) else callLogDao.getInitialCallLogs()
        onLoaded(list as ArrayList<CallLogModel>)

    }


    @JvmStatic
    fun loadCallLogsBy(query: String, onLoaded: (ArrayList<CallLogModel>, Boolean) -> Unit) {
        val search: Boolean
        val list = if (query.isBlank()) {
            search = false
            callLogDao.getInitialCallLogs()
        } else {
            search = true
            val number = query.toIntOrNull()
            if (number != null) {
                callLogDao.findCallLogsByNumber("%$query%")
            } else {
                callLogDao.findCallLogsByName("%$query%")
            }
        }
        onLoaded(list as ArrayList<CallLogModel>, search)

    }


    fun getLastCallLog() {
        val contacts = CallLog.Calls.CONTENT_URI
        val managedCursor =
            context.contentResolver.query(contacts, null, null, null, "DATE desc") ?: return

        val number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
        val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
        val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
        val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
        val subscriptionIdC = managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID)
        val id = managedCursor.getColumnIndex(CallLog.Calls._ID)
        if (managedCursor.moveToFirst()) {
            val call = CallLogModel()
            val phNumber = managedCursor.getString(number)
            val callType = managedCursor.getString(type)
            val callDate = managedCursor.getLong(date)
            //  val callDayTime = Date(callDate).toString()
            val name =
                managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
            val image =
                managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_ID))
            // long timestamp = convertDateToTimestamp(callDayTime);
            val callDuration = managedCursor.getString(duration)
            val subscriptionId = managedCursor.getString(subscriptionIdC)
            val sid = managedCursor.getLong(id)
            val dircode = callType.toInt()
            val dir = when (dircode) {
                CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                CallLog.Calls.INCOMING_TYPE -> "INCOMING"
                CallLog.Calls.MISSED_TYPE -> "MISSED"
                else -> "OUTGOING"
            }
            //  sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration
            //  +" \nname :--- " + name)
            // sb.append("\n----------------------------------")
            call.callType = dir
            call.callDate = callDate
            call.phNumber = phNumber
            call.sim = getSimSlot(subscriptionId)
            call.name = name
            call.id = sid
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
            callLogDao.findCallLogById(sid)?.also {
                callLogDao.update(call)
                Log.d("Last Call Log Updated", "id = ${it.id}")
            } ?: run {
                callLogDao.insert(call)
                Log.d("New Call Log Added", call.id.toString())
            }
        }
        managedCursor.close()
        //System.out.println(sb);


    }

    @JvmStatic
    fun fetchCallHistory(
        numbers: ArrayList<String>,
        onCallHistoryListener: FetchCallHistory.OnCallHistoryListener
    ) {
        GlobalScope.launch {
            val callLogs = ArrayList<CallLogModel>(numbers.size)
            numbers.forEach {
                callLogs.addAll(getCallLogs(it))
            }
            withContext(Dispatchers.Main) {
                onCallHistoryListener.onCallHistory(callLogs)
            }
        }
    }

    @JvmStatic
    fun fetchAllMessages(onMessagesListener: FetchMessages.OnMessagesListener) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            GlobalScope.launch {
                val smsList = getAllSms()

                withContext(Dispatchers.Main) {
                    onMessagesListener.onMessages(smsList)
                }
            }
        } else FetchMessages(onMessagesListener).execute()
    }

    @JvmStatic
    fun getContactByNumber(number: String, callback: (Contact?) -> Unit) {
        GlobalScope.launch {
            val contacts = contactsDao.findContactsByNumbers(listOf("%$number%"))
            contacts.find { contact ->
                contact.numbers.find {
                    number == it
                } != null
            }.also(callback)
        }

    }

    @JvmStatic
    fun getCallLogByNumber(id: Long, callback: (CallLogModel) -> Unit) {
        GlobalScope.launch {
            callLogDao.findCallLogById(id)?.also(callback)
        }

    }


/* @JvmStatic
 fun getContactByName(name: String, callback: (Contact?) -> Unit) {
     GlobalScope.launch {
         callback(getContacts().find { it.name == name })
     }
 }*/

/*   @JvmStatic
   fun fetchContactsByNumber(number: String, onComplete: (ArrayList<Contact>) -> Unit) {
       GlobalScope.launch {
           val filtered = getContacts().filter {
               ContactUtils.formatNumberToLocal(it.getNumber().replace(" ", "")) == ContactUtils.formatNumberToLocal(number.replace(" ", ""))
           }
           withContext(Dispatchers.Main)
           {
               onComplete(ArrayList(filtered))
           }
       }
   }*/

/*@JvmStatic
fun fetchCallLog(onCallLogListener: FetchCallLog.OnCallLogListener) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
        GlobalScope.launch {
            val callLogList = getCallDetails()
            withContext(Dispatchers.Main) {
                onCallLogListener.onCallLog(callLogList)
            }
        }
    } else {
        FetchCallLog(onCallLogListener).execute()
    }
}

@JvmStatic
fun fetchContacts(onContactsListener: FetchContacts.OnContactsListener) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
        GlobalScope.launch {
            val contacts = getContacts()
            withContext(Dispatchers.Main) {
                onContactsListener.onContacts(contacts)
            }
        }
    } else {
        if (contacts.isEmpty())
            FetchContacts(onContactsListener).execute()
        else onContactsListener.onContacts(contacts)
    }
}*/


    fun getWhatsAppUserId(name: String, callType: String, callback: (Long) -> Unit) {
        GlobalScope.launch {
            val resolver = context.contentResolver
            val cursor = resolver.query(
                ContactsContract.Data.CONTENT_URI,
                null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME
            )
            var found = false
            while (cursor?.moveToNext() == true) {
                val id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID))
                val displayName =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME))
                val mimeType =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE))
                found = displayName == name && mimeType == callType

                if (found) {
                    Log.d(
                        this@AppAsyncWorker.javaClass.simpleName,
                        "$id $displayName $mimeType"
                    )
                    callback(id)
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

    fun getLastContact(uid: Int) {

        if (uid == -1)
            return

        val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        val cr = context.contentResolver
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (cursor != null && cursor.count > 0 && cursor.moveToLast()) {
            //moving cursor to last position
            //to get last element added
            val contactName =
                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            val contactNumbers = ArrayList<String>(50)
            val id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val phoneNumbersFound =
                cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0

            // get the user's email address
            var contactEmail: String? = null
            val ce: Cursor? = cr.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                arrayOf(id.toString()),
                null
            )
            if (ce != null && ce.moveToFirst()) {
                contactEmail =
                    ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                ce.close()
            }

            if (phoneNumbersFound) {
                val pCur = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(id.toString()),
                    null
                )
                while (pCur?.moveToNext() == true) {
                    val contactNumber =
                        pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    if (!contactNumber.isNullOrBlank()) {
                        contactNumbers.add(contactNumber.replace(" ", ""))
                    }


                }
                pCur?.close()


                val msg =
                    "Name : $contactName Id:$id Contact No. : $contactNumbers email: $contactEmail"

                Log.d("Last Contact Detected", msg)

            }
            cursor.close()

            val contact = Contact().apply {
                contactId = id
                email = contactEmail
                name = contactName
                numbers = ArrayList(HashSet(contactNumbers))
                if (numbers.isNotEmpty())
                    number1 = contactNumbers[0]
            }

            contactsDao.findContactById(id)?.also {
                contactsDao.update(contact)
                Log.d("Last Contact Updated", "id = ${it.contactId}")
            } ?: run {
                contactsDao.insert(contact)
                contact.contactId.let { Log.d("New Contact Added", it.toString()) }
                apiInterface.uploadContactsSync(UploadContactsRequest(arrayListOf(contact), uid))
                    .execute()
            }


        }


    }


    private fun getContacts(): ArrayList<Contact> {
        val contacts = ArrayList<Contact>(500)
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
        )
        val filter = "${ContactsContract.Contacts.DISPLAY_NAME} NOT LIKE '%@%'"
        val order =
            String.format("%1\$s COLLATE NOCASE", ContactsContract.Contacts.DISPLAY_NAME)
        val cr = context.contentResolver
        val cursor: Cursor? =
            cr.query(ContactsContract.Contacts.CONTENT_URI, projection, filter, null, order)
        if (cursor?.moveToFirst() == true) {

            while (cursor.moveToNext()) {
                // get the contact's information
                val id =
                    cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name: String =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhone: Int =
                    cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                // get the user's email address
                var email = ""
                val ce: Cursor? = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    arrayOf(id.toString()),
                    null
                )
                if (ce != null && ce.moveToFirst()) {
                    email =
                        ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                    ce.close()
                }
                // get the user's phone number
                var phone: String? = null
                var image: String? = null
                val numbers = ArrayList<String>(50)
                if (hasPhone > 0) {
                    val cp: Cursor? = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id.toString()),
                        null
                    )
                    if (cp?.moveToFirst() == true) {
                        image =
                            cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI))

                        while (cp.moveToNext()) {
                            val phoneNumber =
                                cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            if (phone.isNullOrBlank())
                                phone = phoneNumber
                            if (phoneNumber.isNotBlank())
                                numbers.add(phoneNumber.replace(" ", ""))
                        }
                        Log.i(javaClass.simpleName, "Contact: $name $phone")
                        cp.close()
                    }
                }
                // if the user user has an email or phone then add it to contacts
                if ((!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches()
                            && !email.equals(name, ignoreCase = true)) || !TextUtils.isEmpty(
                        phone
                    )
                ) {
                    val contact = Contact()
                    contact.name = name
                    contact.email = email
                    //    contact.number = phone
                    contact.image = image
                    contact.contactId = id
                    val list = ArrayList(HashSet(numbers))
                    if (list.isNotEmpty())
                        contact.number1 = list[0]
                    if (list.size > 1)
                        contact.number2 = list[1]
                    contact.numbers = list
                    contacts.add(contact)
                    //  val cid = contact.save()
                    //Log.d("ContactID", cid.toString())
                }
            }
        }
        // clean up cursor
        cursor?.close()

        //   ActiveAndroid.setTransactionSuccessful()
        // ActiveAndroid.endTransaction()
        return contacts
    }


    fun getCallLogs(): ArrayList<CallLogModel> {
        val managedCursor =
            context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                CallLog.Calls.NUMBER + "," + CallLog.Calls.DATE + " DESC"
            )
        val callLogModelList = ArrayList<CallLogModel>(managedCursor!!.count)
        val number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
        val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
        val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
        val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
        val subscriptionIdC = managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID)
        val id = managedCursor.getColumnIndex(CallLog.Calls._ID)
        // sb.append("Call Details :")
        while (managedCursor.moveToNext()) { //  HashMap rowDataCall = new HashMap<String, String>();
            val call = CallLogModel()
            val phNumber = managedCursor.getString(number)
            val callType = managedCursor.getString(type)
            val callDate = managedCursor.getLong(date)
            // val callDayTime = Date(callDate).toString()
            val name =
                managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
            val image =
                managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_ID))
            // long timestamp = convertDateToTimestamp(callDayTime);
            val callDuration = managedCursor.getString(duration)
            val subscriptionId = managedCursor.getString(subscriptionIdC)
            val sid = managedCursor.getLong(id)
            val dircode = callType.toInt()
            val dir = when (dircode) {
                CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                CallLog.Calls.INCOMING_TYPE -> "INCOMING"
                CallLog.Calls.MISSED_TYPE -> "MISSED"
                else -> "OUTGOING"
            }
            //  sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration
            //  +" \nname :--- " + name)
            // sb.append("\n----------------------------------")
            call.callType = dir
            call.callDate = callDate
            call.phNumber = phNumber
            // call.callDayTime = callDayTime
            call.sim = getSimSlot(subscriptionId)
            call.name = name
            call.id = sid
            call.image = image
            // call.email = getEmail()
            val hours = Integer.valueOf(callDuration) / 3600
            val minutes = Integer.valueOf(callDuration) % 3600 / 60
            val seconds = Integer.valueOf(callDuration) % 60
            call.callDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            Log.i(javaClass.simpleName, "CallLog: $name $phNumber")
            callLogModelList.add(call)
        }
        managedCursor.close()
        //System.out.println(sb);
        return callLogModelList
    }

    @SuppressLint("MissingPermission")
    private fun getSimSlot(subscriptionId: String): String {
        val sm =
            context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        try {
            if (sm.activeSubscriptionInfoCount > 1) {
                sm.activeSubscriptionInfoList.find {
                    subscriptionId.substring(1, subscriptionId.lastIndex) == it.iccId
                }?.let {
                    return it.simSlotIndex.toString()
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

        return "0"
    }

    fun getAllSms(): ArrayList<Sms> {

        var objSms: Sms
        // Uri message = Uri.parse("content://sms/conversations");
        val cr: ContentResolver = context.contentResolver
        //Telephony.Sms.PERSON;
        val projection = arrayOf(
            "thread_id",
            "MAX(date) as date",
            "COUNT(*) AS msg_count",
            "body",
            "address",
            "(COUNT(*)-SUM(read)) as unread"
        )
        val c = cr.query(
            Telephony.Sms.CONTENT_URI,
            projection,
            "thread_id) GROUP BY (thread_id",
            null,
            null
        )
        DatabaseUtils.dumpCursor(c)
        val totalSMS = c!!.count
        val lstSms = ArrayList<Sms>(totalSMS)
        try {
            if (c.moveToFirst()) {
                for (i in 0 until totalSMS) {
                    objSms = Sms()
                    objSms.id = c.getString(c.getColumnIndexOrThrow("thread_id"))
                    objSms.address = c.getString(
                        c
                            .getColumnIndexOrThrow("address")
                    )
                    objSms.msg = c.getString(c.getColumnIndexOrThrow("body"))
                    objSms.readState = c.getString(c.getColumnIndex("unread"))
                    objSms.time = c.getString(c.getColumnIndexOrThrow("date"))
                    if (c.getString(c.getColumnIndexOrThrow("msg_count")).contains("1")) {
                        objSms.folderName = "inbox"
                    } else {
                        objSms.folderName = "sent"
                    }
                    objSms.name = getContactByPhoneNumber(objSms.address)
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

    private fun getContactByPhoneNumber(phoneNumber: String?): String? {
        val uri: Uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        return if (cursor == null) {
            phoneNumber
        } else {
            var name = phoneNumber
            cursor.use {
                if (it.moveToFirst()) {
                    name =
                        it.getString(it.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
                }
            }
            name
        }
    }


    fun getCallLogs(numbers: String): ArrayList<CallLogModel> {
        val contacts = CallLog.Calls.CONTENT_URI
        val managedCursor =
            context.contentResolver.query(
                contacts,
                null,
                "number=?",
                arrayOf(numbers),
                "DATE asc"
            )
        val number = managedCursor!!.getColumnIndex(CallLog.Calls.NUMBER)
        val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
        val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
        val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
        val subscriptionIdC = managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID)
        // sb.append("Call Details :")
        val callLogModelList = ArrayList<CallLogModel>(managedCursor.count)
        while (managedCursor.moveToNext()) {
            //   val rowDataCall: HashMap<*, *> = HashMap<String, String>()
            val call = CallLogModel()
            val phNumber = managedCursor.getString(number)
            val callType = managedCursor.getString(type)
            val callDate = managedCursor.getLong(date)
            //  val callDayTime = Date(callDate).toString()
            val name =
                managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
            // long timestamp = convertDateToTimestamp(callDayTime);
            val callDuration = managedCursor.getString(duration)
            val subscriptionId = managedCursor.getString(subscriptionIdC)
            val dircode = callType.toInt()
            val dir = when (dircode) {
                CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                CallLog.Calls.INCOMING_TYPE -> "INCOMING"
                CallLog.Calls.MISSED_TYPE -> "MISSED"
                else -> "OUTGOING"
            }
            /*sb.append(
                "\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration
                        + " \nname :--- " + name
            )*/
            //sb.append("\n----------------------------------")
            call.callType = dir
            call.callDate = callDate
            call.phNumber = phNumber
            // call.callDayTime = callDayTime
            call.sim = getSimSlot(subscriptionId)
            call.name = name
            // val hours = Integer.valueOf(callDuration) / 3600
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

    @JvmStatic
    fun checkAppUpdate(onSuccess: () -> Unit) {
        GlobalScope.launch {
            val userId = TrueName.getUserId(context)
            val manager = context.packageManager
            val info = manager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                info.versionCode.toLong()
            }


            val appUpdate = withContext(Dispatchers.IO) {
                try {
                    apiInterface.checkAppUpdate(userId)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            withContext(Dispatchers.Main.immediate) {
                if (appUpdate?.status == "yes" && versionCode < appUpdate.version) {
                    onSuccess()   // app update
                } else {
                    try {
                        apiInterface.notifyAppUpToDate(userId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }


    @JvmStatic
    fun sendDailyInvite(ignoreDayDiff: Boolean) {
        GlobalScope.launch(Dispatchers.IO) {
            if (ignoreDayDiff || getDayDiff() >= 1) {
                val response = apiInterface.invite("add")
                val contacts = contactsDao.getAllContacts()
                sendSMSToPhoneBook(contacts, response)
            }
        }
    }

    @JvmStatic
    fun trackWeeklyLocation(mainActivity: MainActivity) {
        ContactUtils.shareLocation(mainActivity) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                    val userId = TrueName.getUserId(context)
                    if (day == Calendar.WEDNESDAY) {
                        apiInterface.uploadLocation(userId, it.latitude, it.longitude)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}