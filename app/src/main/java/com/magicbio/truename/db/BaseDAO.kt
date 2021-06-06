package com.magicbio.truename.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDAO<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(t: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(ts: List<T>): List<Long>

    @Update
    fun update(t: T)

    @Delete
    fun delete(t: T)

    @Delete
    fun deleteAll(ts: List<T>)
}