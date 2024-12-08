package com.dicoding.rupismart_app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatDelegate
import com.dicoding.rupismart_app.R
import com.google.android.material.switchmaterial.SwitchMaterial
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun ThemeisDark(isDarkModeActive: Boolean, switchTheme: SwitchMaterial? = null) {
    if (isDarkModeActive) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        switchTheme?.isChecked = true
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        switchTheme?.isChecked = false
    }
}
fun String.formatTimestamp(context: Context): String {
    val timestamp = this.toLong()
    val currentTime = System.currentTimeMillis()

    val timeDiff = currentTime - timestamp

    val locale = Locale.getDefault() 

    return when {
        timeDiff < DateUtils.MINUTE_IN_MILLIS -> {
          context.getString(R.string.just_now)
        }
        timeDiff < DateUtils.HOUR_IN_MILLIS -> {
            val minutes = timeDiff / DateUtils.MINUTE_IN_MILLIS
           context .getString(R.string.min_ago, minutes.toString())
        }
        timeDiff < DateUtils.DAY_IN_MILLIS -> {
            val hours = timeDiff / DateUtils.HOUR_IN_MILLIS
            context.getString(R.string.hour_ago, hours.toString())
        }
        timeDiff < DateUtils.DAY_IN_MILLIS * 2 -> {
             context.getString(R.string.yesterday)
        }
        else -> {
            val sdf = SimpleDateFormat("d MMMM yyyy", locale)
            sdf.format(Date(timestamp))
        }
    }
}
fun reduceIMage(file:File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength:Int
    do{
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,compressQuality,bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -=5
    }while(streamLength> MAXIMAL_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG,compressQuality, FileOutputStream(file))
    return file
}
fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength:Int
    do{
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,compressQuality,bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -=5
    }while(streamLength> MAXIMAL_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG,compressQuality, FileOutputStream(file))
    return file
}

private const val MAXIMAL_SIZE = 1000000