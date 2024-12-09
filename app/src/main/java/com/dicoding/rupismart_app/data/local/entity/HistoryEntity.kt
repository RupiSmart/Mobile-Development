package com.dicoding.rupismart_app.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize

@Entity(tableName = "history")
data class HistoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nominal")
    val nominal: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "confidence")
    val confidence: String,

    @ColumnInfo(name = "label")
    val label: String,

    @ColumnInfo(name = "img")
    val img: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: String
) : Parcelable
