package com.magicbio.truename.db.calllog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.magicbio.truename.db.BaseDAO
import com.magicbio.truename.models.CallLogModel

@Dao
interface CallLogDao : BaseDAO<CallLogModel> {
    @Insert
    fun addCallLog(contacts: List<CallLogModel>): List<Long>

    @Query("select * from callLog")
    fun getAllCallLog(): List<CallLogModel>

    @Query("select * from callLog limit 50")
    fun getInitialCallLogs(): List<CallLogModel>

    @Query("select * from callLog limit 50 offset :offset")
    fun getCallLogs(offset: Int = 0): List<CallLogModel>

    @Query("select * from callLog limit 1")
    fun get1stCallLog(): CallLogModel?

    @Query("SELECT * FROM callLog WHERE name OR phNumber LIKE :query limit 50")
    fun findCallLogs(query: String): List<CallLogModel>

    @Query("select * from callLog where id = :id ")
    fun findCallLogById(id: Long): CallLogModel?

    @Query("delete from callLog")
    fun deleteAll()

}