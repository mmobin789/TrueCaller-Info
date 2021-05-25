package com.magicbio.truename.db.contacts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.magicbio.truename.db.BaseDAO
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao : BaseDAO<Contact> {
    @Insert
    suspend fun addContacts(contacts: List<Contact?>): List<Long>

    @Query("select * from contacts where id between :startId and :endId")
    fun getContactsIn(startId: Int, endId: Int): Flow<List<Contact>>

    @Query("select * from contacts")
    fun getAllContacts(): List<Contact>

    @Query("SELECT * FROM contacts WHERE name LIKE :name")
    fun findContactsByName(name: String): Flow<List<Contact>>

    @Query("select * from contacts where id = :id ")
    suspend fun findContactById(id: Int): Contact
}