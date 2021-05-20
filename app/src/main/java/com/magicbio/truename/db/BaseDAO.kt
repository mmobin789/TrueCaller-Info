package com.magicbio.truename.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDAO<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(t: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ts: List<T>): List<Long>

    @Update
    suspend fun update(t: T)

    @Delete
    suspend fun delete(t: T)

    @Delete
    suspend fun deleteAll(ts: List<T>)
}