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
import com.dicoding.rupismart_app.utils.imageProxyToBitmap
import com.dicoding.rupismart_app.utils.reduceIMage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.math.log

class ScanFragment : Fragment() {
    private var resultSpeech:String=""
    private val viewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var onProcess = false
    private var notificationActive = false
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
//                startCamera()
                startClassification()
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
        startClassification()
        startAction()
//        startCamera()

   }
//clasification
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var lastClassificationTime = 0L
    private fun startClassification() {
        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onResults(
                    results: List<Classifications>?,
                    inferenceTime: Long
                ) {
                    activity?.runOnUiThread {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClassificationTime >= 4000) {
                            results?.let {
                                if (it.isNotEmpty()) {
                                    val sortedResults = it.sortedByDescending { classification -> classification.score }
                                    val displayResult = sortedResults.joinToString("\n") { result ->
                                        if(result.score < .85f){
                                            resultSpeech="sedih"
                                            return@joinToString "Agus Sedih bangett :("
                                        }

                                         val resultText = "${result.label}: ${NumberFormat.getPercentInstance().format(result.score).trim()}"
                                            resultSpeech = result.label + "Rupiah"

                                              resultText
                                    }


                                    lastClassificationTime = currentTime
                                } else {
                                    lastClassificationTime = currentTime
                                }
                                viewLifecycleOwner.lifecycleScope.launch {
                                    binding.notificationResult.visibility = View.VISIBLE
                                    binding.nominalNumber.text = resultSpeech
                                    SoundPoolPlayer.playSound(R.raw.popup)
                                    tspeech(resultSpeech)
                                    delay(1000)
                                    binding.notificationResult.visibility = View.GONE
                                }

                            }
                    }
                }
                }
            }
        )

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()
            val imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
            imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                imageClassifierHelper.classifyImage(image)

            }

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
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

//            SoundPoolPlayer.playSound(R.raw.click)
//            if (!onProcess) {
////                shutterOn()
//
//            } else {
//                tspeech(getString(R.string.on_process))
//            }
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
                    viewModel.upload(requireContext(), p).observe(viewLifecycleOwner){result->
                        when (result) {
                            is Result.Success -> {
                                photoFile.delete()
                                binding.progressIndicator.visibility = View.GONE
                                viewLifecycleOwner.lifecycleScope.launch {
                                    binding.notificationResult.visibility = View.VISIBLE
                                    binding.nominalNumber.text = result.data.result.authenticity
                                    SoundPoolPlayer.playSound(R.raw.popup)
                                    tspeech("Uang"+result.data.result.authenticity)
                                    delay(2000)
                                    binding.notificationResult.visibility = View.GONE
                                    onProcess = false
                                }
                            }

                            is Result.Error -> {
                                photoFile.delete()
                                onProcess = false
                                tspeech(getString(R.string.fail_to_upload_scan))
                                tspeech(result.error)
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
//        startCamera()
//        startClassification()
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
