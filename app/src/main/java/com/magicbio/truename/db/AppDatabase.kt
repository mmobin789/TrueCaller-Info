package com.magicbio.truename.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.magicbio.truename.db.calllog.CallLogDao
import com.magicbio.truename.db.contacts.Contact
import com.magicbio.truename.db.contacts.ContactDao
import com.magicbio.truename.models.CallLogModel

@Database(entities = [Contact::class,CallLogModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao

    abstract fun callLogDao(): CallLogDao
}