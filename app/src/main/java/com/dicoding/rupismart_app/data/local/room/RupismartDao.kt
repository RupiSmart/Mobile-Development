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

@Query("DELETE FROM history WHERE id = (SELECT id FROM history ORDER BY timestamp ASC LIMIT 1)")
fun deleteLatestHistory()

@Insert(onConflict = OnConflictStrategy.IGNORE)
 fun insertHistory(history: HistoryEntity)

 @Query("DELETE FROM history WHERE id = :id")
 fun deleteHistoryById(id: Int)

 @Query("SELECT COUNT(*) FROM history")
  fun getHistoryCount(): Int
}