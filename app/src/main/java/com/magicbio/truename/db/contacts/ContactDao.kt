package com.magicbio.truename.db.contacts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.magicbio.truename.db.BaseDAO

@Dao
interface ContactDao : BaseDAO<Contact> {
    @Insert
    fun addContacts(contacts: List<Contact?>): List<Long>

    @Query("select * from contacts where id between :startId and :endId")
    fun getContactsIn(startId: Int, endId: Int): List<Contact>

    @Query("select * from contacts")
    fun getAllContacts(): List<Contact>

    @Query("select * from contacts limit 1")
    fun get1stContact(): Contact?

    @Query("SELECT * FROM contacts WHERE name LIKE :name")
    fun findContactsByName(name: String): List<Contact>

    @Query("select * from contacts where contactId = :id")
    fun findContactById(id: Int): Contact?

    @Query("delete from contacts")
    fun deleteAll()
}