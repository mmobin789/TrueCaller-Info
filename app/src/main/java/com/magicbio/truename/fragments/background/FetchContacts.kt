package com.magicbio.truename.fragments.background

import android.content.Context
import android.database.DatabaseUtils
import android.os.AsyncTask
import android.provider.ContactsContract
import com.activeandroid.ActiveAndroid
import com.magicbio.truename.activeandroid.Contact

class FetchContacts : AsyncTask<Context, Unit, ArrayList<Contact>>() {

    var onComplete: ((MutableList<Contact>) -> Unit)? = null

    override fun doInBackground(vararg params: Context?): ArrayList<Contact> {
        return getContactList(params[0]!!)
    }

    override fun onPostExecute(result: ArrayList<Contact>) {
        onComplete?.invoke(result)
    }

    private fun getContactList(context: Context): ArrayList<Contact> {
        var contactList: ArrayList<Contact>? = null

        if (!Contact.getAll().isNullOrEmpty()) {
            contactList = Contact.getAll() as ArrayList<Contact>
            return contactList
        }

        val cr = context.contentResolver
        val cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null)
        DatabaseUtils.dumpCursor(cur)
        ActiveAndroid.beginTransaction()
        if (cur?.count ?: 0 > 0) {
            contactList = ArrayList(cur!!.count)
            while (cur.moveToNext()) {
                //  val contactModel = ContactModel()
                val contact = Contact()
                val id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME))
                if (cur.getInt(cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)
                    while (pCur!!.moveToNext()) {
                        val phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        val image = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                        //     contactModel.name = name
                        //     contactModel.image = image
                        //    contactModel.number = phoneNo
                        try {
                            contact.setName(name)
                            contact.setNumber(phoneNo)
                            contact.image = image
                            contact.save()
                            contactList.add(contact)

                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }

                    }
                    pCur.close()
                }
            }
        }
        cur?.close()
        ActiveAndroid.setTransactionSuccessful()
        ActiveAndroid.endTransaction()

        return contactList!!
    }


}