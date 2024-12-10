package com.dicoding.rupismart_app.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.rupismart_app.data.local.entity.HistoryEntity

@Dao
interface RupismartDao
{
@Query("SELECT * FROM history ORDER BY timestamp DESC")
fun getAllHistory(): LiveData<List<HistoryEntity>>

@Query("SELECT * FROM history ORDER BY timestamp DESC LIMIT 1")
suspend fun getLatestHistory(): HistoryEntity?

@Insert(onConflict = OnConflictStrategy.IGNORE)
 fun insertHistory(history: HistoryEntity)

@Query("DELETE FROM history WHERE id = :id")
suspend fun deleteAnalysisById(id: Int)

}