package com.magicbio.truename.db.calllog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.magicbio.truename.db.BaseDAO
import com.magicbio.truename.models.CallLogModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CallLogDao : BaseDAO<CallLogModel> {
    @Insert
    suspend fun addCallLog(contacts: List<CallLogModel>): List<Long>

    @Query("select * from callLog where id between :startId and :endId")
    fun getCallLogIn(startId: Int, endId: Int): Flow<List<CallLogModel>>

    @Query("SELECT * FROM callLog WHERE name LIKE :name")
    fun findCallLogByName(name: String): Flow<List<CallLogModel>>

    @Query("select * from callLog where _id = :id ")
    fun findCallLogById(id: String): CallLogModel?

}