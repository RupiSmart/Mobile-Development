package com.dicoding.rupismart_app.helper

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.dicoding.rupismart_app.ml.Model
import com.dicoding.rupismart_app.helper.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class ImageClassifierHelper(
    private var threshold: Float = 0.1f,
    val context: Context,
    private val classifierListener: ClassifierListener?
) {
    private var model: Model? = null

    init {
        setupModel()
    }

    private fun setupModel() {
        try {
            model = Model.newInstance(context)
        } catch (e: Exception) {
            classifierListener?.onError("Failed to load model: ${e.message}")
            Log.e(TAG, e.message.toString())
        }
    }

    fun classifyImage(image: ImageProxy) {
        if (model == null) {
            setupModel()
        }

        val bitmap = toBitmap(image)

        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(120, 120, ResizeOp.ResizeMethod.BILINEAR))
            .build()
        val processedImage = imageProcessor.process(tensorImage)

        val inputFeature = TensorBuffer.createFixedSize(intArrayOf(1, 120, 120, 3), DataType.FLOAT32)
        inputFeature.loadBuffer(processedImage.buffer)

        var inferenceTime = SystemClock.uptimeMillis()
        val outputs = model?.process(inputFeature)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime

        val outputFeature = outputs?.outputFeature0AsTensorBuffer
        val result = parseResults(outputFeature, threshold)

        classifierListener?.onResults(result?.let { listOf(it) }, inferenceTime)
    }

    private fun parseResults(output: TensorBuffer?, threshold: Float): Classifications? {
        if (output == null) return null
        val labels = listOf(
            "100",
            "500",
            "100k",
            "10k",
            "1k",
            "2k",
            "20k",
            "2k",
            "20k",
            "50k",
            "5k",
            "75k"
        )
        val scores = output.floatArray

        val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: return null
        val maxScore = scores[maxIndex]

        return if (maxScore >= threshold && maxIndex < labels.size) {
            Classifications(labels[maxIndex], maxScore)
        } else {
            null
        }
    }

    private fun toBitmap(image: ImageProxy): Bitmap {
        val bitmapBuffer = Bitmap.createBitmap(
            image.width,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
        image.close()
        return bitmapBuffer
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?,
            inferenceTime: Long
        )
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}