package com.magicbio.truename.db.contacts

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,
    @Expose
    var name: String = "",
    @Expose
    var numbers: ArrayList<String> = arrayListOf(),

    var image: String? = null,

    var email: String? = null,

    var contactId: Int = 0,       // this is the id of this contact in the phonebook db.
    @Ignore
    var showAd: Boolean = false,
    @Ignore
    var areOptionsShown: Boolean = false
)