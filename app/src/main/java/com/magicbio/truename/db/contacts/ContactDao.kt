package com.magicbio.truename.db.contacts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.magicbio.truename.db.BaseDAO

@Dao
interface ContactDao : BaseDAO<Contact> {
    @Insert
    fun addContacts(contacts: List<Contact?>): List<Long>

    @Query("select * from contacts order by name limit 50")
    fun getInitialContacts(): List<Contact>

    @Query("select * from contacts order by name limit 50 offset :offset")
    fun getContacts(offset: Int): List<Contact>

    @Query("select * from contacts")
    fun getAllContacts(): List<Contact>

    @Query("SELECT * FROM contacts WHERE name LIKE :name LIMIT 50")
    fun findContactsByName(name: String): List<Contact>

    @Query("SELECT * FROM contacts WHERE numbers LIKE :numbers LIMIT 50")
    fun findContactsByNumbers(numbers: List<String>): List<Contact>

    @Query("SELECT * FROM contacts where number1 = :number or number2 = :number")
    fun findContactByNumber(number: String): Contact?

    @Query("select * from contacts where contactId = :id")
    fun findContactById(id: Int): Contact?


   /* @Query("delete from contacts")
    fun deleteAll()*/
}