package com.dicoding.rupismart_app.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.rupismart_app.data.local.entity.HistoryEntity
import com.dicoding.rupismart_app.data.local.room.RupismartDao


@Database(entities = [HistoryEntity::class], version = 1, exportSchema = false)
    abstract class RupismartDatabase : RoomDatabase() {
        abstract fun rupismartDao(): RupismartDao
        companion object {
            @Volatile
            private var instance: RupismartDatabase? = null
            fun getInstance(context: Context): RupismartDatabase =
                instance ?: synchronized(this) {
                    instance ?: Room.databaseBuilder(
                        context.applicationContext,
                        RupismartDatabase::class.java, "Rupismart.db"
                    ).allowMainThreadQueries().build()
                }
        }
    }
