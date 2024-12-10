package com.dicoding.rupismart_app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.AudioAttributes
import android.media.Image
import android.media.SoundPool
import android.text.format.DateUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.ImageProxy
import com.dicoding.rupismart_app.R
import com.dicoding.rupismart_app.data.remote.response.PredictionTime
import com.google.android.material.switchmaterial.SwitchMaterial
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun ThemeisDark(isDarkModeActive: Boolean, switchTheme: SwitchMaterial? = null) {
    if (isDarkModeActive) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        switchTheme?.isChecked = true
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        switchTheme?.isChecked = false
    }
}
 fun ImageProxy.imageProxyToBitmap(): Bitmap {
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
    val byteArray = out.toByteArray()
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
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
fun imageProxyToBitmap(imageProxy: ImageProxy) {
 /*   val image: Image = imageProxy.image ?: return null
    val buffer: ByteBuffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    // Decode byte array to bitmap
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    // Close image proxy when done
    imageProxy.close()

    return bitmap*/
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