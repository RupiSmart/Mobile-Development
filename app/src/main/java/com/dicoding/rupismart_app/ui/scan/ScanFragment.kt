package com.dicoding.rupismart_app.ui.scan

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.SoundPool
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.rupismart_app.R
import com.dicoding.rupismart_app.ViewModelFactory
import com.dicoding.rupismart_app.data.Result
import com.dicoding.rupismart_app.databinding.FragmentScanBinding
import com.dicoding.rupismart_app.helper.Classifications
import com.dicoding.rupismart_app.helper.ImageClassifierHelper
import com.dicoding.rupismart_app.ui.setting.SettingActivity
import com.dicoding.rupismart_app.utils.SoundPoolPlayer
import com.dicoding.rupismart_app.utils.reduceIMage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.Executors

class ScanFragment : Fragment() {
    private val viewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var onProcess = false
    private lateinit var tts: TextToSpeech
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private lateinit var photoFile: File

    private var spLoaded = false
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var imageCapture: ImageCapture

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.permission_request_denied), Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        if (!allPermissionGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        SoundPoolPlayer.initialize(requireContext(),listOf(R.raw.click,R.raw.popup))
        binding.mainAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.setting -> {
                    startActivity(Intent(requireContext(), SettingActivity::class.java))
                    true
                }
                else -> false
            }
        }

        startAction()
        startCamera()

        viewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    photoFile.delete()
                    val nominalNumber = result.data.result.nominal
                    val nominalText = result.data.result.nominalText
                    binding.progressIndicator.visibility = View.GONE
                    viewLifecycleOwner.lifecycleScope.launch {
                        binding.notificationResult.visibility = View.VISIBLE
                        binding.nominalNumber.text = nominalNumber
                        binding.nominalText.text=nominalText
                        SoundPoolPlayer.playSound(R.raw.popup)
                        tspeech(nominalText)
                        delay(2000)
                        binding.notificationResult.visibility = View.GONE
                        onProcess = false
                    }
                }

                is Result.Error -> {
                    photoFile.delete()
                    onProcess = false
                    tspeech(getString(R.string.fail_to_upload_scan))
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(),
                        getString(R.string.fail_to_get_img), Toast.LENGTH_SHORT)
                        .show()
                }
                is Result.Loading -> {
                    onProcess = true
                    binding.progressIndicator.visibility = View.VISIBLE
                }
            }
        }
    }

    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var lastClassificationTime = 0L
    private fun startClassification() {
//        imageClassifierHelper = ImageClassifierHelper(
//            context = this,
//            classifierListener = object : ImageClassifierHelper.ClassifierListener {
//                override fun onError(error: String) {
//                    runOnUiThread {
//                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                @SuppressLint("SetTextI18n")
//                override fun onResults(
//                    results: List<Classifications>?,
//                    inferenceTime: Long
//                ) {
//                    val currentTime = System.currentTimeMillis()
//                    if (currentTime - lastClassificationTime >= 3000) {
//                        runOnUiThread {
//                            results?.let {
//                                if (it.isNotEmpty()) {
//                                    val sortedResults = it.sortedByDescending { classification -> classification.score }
//                                    val displayResult = sortedResults.joinToString("\n") { result ->
//                                        if(result.score < 0.85f){
//                                            speakText("Try again later.")
//                                            return@joinToString "Agus Sedih bangett :("
//                                        }
//                                        val resultText = "${result.label}: ${NumberFormat.getPercentInstance().format(result.score).trim()}"
//                                        speakText(resultText)
//                                        resultText
//                                    }
//                                    binding.percentageText.text = displayResult
//                                    binding.hintText.text = "$inferenceTime ms"
//                                    lastClassificationTime = currentTime
//                                } else {
//                                    binding.percentageText.text = "No results"
//                                    binding.hintText.text = ""
//                                    lastClassificationTime = currentTime
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        )
//
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFuture.addListener({
//            val resolutionSelector = ResolutionSelector.Builder()
//                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
//                .build()
//            val imageAnalyzer = ImageAnalysis.Builder()
//                .setResolutionSelector(resolutionSelector)
//                .setTargetRotation(binding.viewFinder.display.rotation)
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//                .build()
//            imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
//                imageClassifierHelper.classifyImage(image)
//            }
//
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//            val preview = Preview.Builder().build().also {
//                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//            }
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    this,
//                    cameraSelector,
//                    preview,
//                    imageAnalyzer
//                )
//            } catch (exc: Exception) {
//                Toast.makeText(
//                    this@MainActivity,
//                    "Gagal memunculkan kamera.",
//                    Toast.LENGTH_SHORT
//                ).show()
//                Log.e(TAG, "startCamera: ${exc.message}")
//            }
//        }, ContextCompat.getMainExecutor(this))
    }

    private fun tspeech(message: String) {
        if (!::tts.isInitialized) {
            tts = TextToSpeech(requireContext(), TextToSpeech.OnInitListener {
                if (it == TextToSpeech.SUCCESS) {
                    val defaultLocale = Locale.getDefault()
                    tts.language = defaultLocale
                    tts.setSpeechRate(1.0f)
                    tts.speak(message, TextToSpeech.QUEUE_ADD, null, null)
                }
            })
        } else {
            tts.speak(message, TextToSpeech.QUEUE_ADD, null, null)
        }
    }

    private fun startAction() {

        binding.viewFinder.setOnClickListener {

            SoundPoolPlayer.playSound(R.raw.click)
            if (!onProcess) {
                shutterOn()

            } else {
                tspeech(getString(R.string.on_process))
            }
        }
    }

    private fun shutterOn() {
        val imgName = "IMG_${System.currentTimeMillis()}.jpg"
        val photoFile = File(
            requireContext().getExternalFilesDir(null),
            imgName
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Image capture failed: ${exc.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val p = reduceIMage(photoFile)
                    setPhotoFile(p)
                    viewModel.uploadImage(requireContext(), p)
                }
            }
        )
    }

    private fun setPhotoFile(p: File) {
        photoFile = p
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = binding.viewFinder.surfaceProvider
                }

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(binding.viewFinder.display.rotation)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(requireContext(),
                    getString(R.string.show_camera_failed), Toast.LENGTH_SHORT).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun playAnimation() {
        val appBar = ObjectAnimator.ofFloat(binding.appBarLayout, View.ALPHA, 1F).setDuration(400)
        AnimatorSet().apply {
            playSequentially(appBar)
            startDelay = 500
            start()
        }
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        SoundPoolPlayer.release()
    }

    companion object {
        private const val TAG = "CameraActivity"
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
