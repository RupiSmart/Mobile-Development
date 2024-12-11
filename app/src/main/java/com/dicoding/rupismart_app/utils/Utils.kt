package com.dicoding.rupismart_app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.SoundPool
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

fun themeIsDark(isDarkModeActive: Boolean, switchTheme: SwitchMaterial? = null) {
    if (isDarkModeActive) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        switchTheme?.isChecked = true
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        switchTheme?.isChecked = false
    }
}

@SuppressLint("StringFormatInvalid")
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
           context.getString(R.string.min_ago, minutes.toString())
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

private const val MAXIMAL_SIZE = 1000000

object SoundPoolPlayer {
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<Int, Int>()

    fun initialize(context: Context, soundResIds: List<Int>) {
        if (soundPool == null) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build()

            soundResIds.forEach { resId ->
                val soundId = soundPool?.load(context, resId, 1) ?: 0
                soundMap[resId] = soundId
            }
        }
    }

    fun playSound(resId: Int) {
        soundMap[resId]?.let { soundId ->
            soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        soundMap.clear()
    }
}
fun getNominal(index: Int): String {
    return when (index) {
        0 -> "IDR 100.00"
        1 -> "IDR 500.00"
        2 -> "IDR 100,000.00"
        3 -> "IDR 10,000.00"
        4 -> "IDR 1,000.00"
        5 -> "IDR 200.00"
        6 -> "IDR 20,000.00"
        7 -> "IDR 2,000.00"
        8 -> "IDR 20,000.00"
        9 -> "IDR 50,000.00"
        10 -> "IDR 5,000.00"
        11 -> "IDR 75,000.00"
        else -> ""
    }

}