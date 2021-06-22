package com.magicbio.truename.db.contacts

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "contacts")
data class Contact(
    @Expose
    var name: String = "",
    @Expose
    var numbers: ArrayList<String> = arrayListOf(),
    @Ignore
    var image: String? = null,
    var email: String? = null,
    var number1: String? = null,
    var number2: String? = null,
    @PrimaryKey
    var contactId: Int = 0,       // this is the id of this contact in the phonebook db.
    @Ignore
    var showAd: Boolean = false,
    @Ignore
    var areOptionsShown: Boolean = false
) {
    override fun equals(other: Any?): Boolean {

        if (other is Contact && other.name == name && other.numbers == numbers)
            return true

        return false
    }

    override fun hashCode(): Int {
        return contactId
    }
}