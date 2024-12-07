package com.dicoding.rupismart_app.ui.scan

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.rupismart_app.R
import com.dicoding.rupismart_app.ViewModelFactory
import com.dicoding.rupismart_app.data.Result
import com.dicoding.rupismart_app.databinding.FragmentScanBinding
import com.dicoding.rupismart_app.ui.setting.SettingActivity
import com.dicoding.rupismart_app.utils.reduceIMage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
class ScanFragment : Fragment() {
    private val viewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var onProcess = false
    private lateinit var tts: TextToSpeech
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private lateinit var photoFile: File

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var imageCapture: ImageCapture

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG).show()
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
                    photoFile?.delete()
                    val nominal = result.data.result.nominal
                    binding.progressIndicator.visibility = View.GONE
                    lifecycleScope.launch {
                        binding.notificationResult.visibility = View.VISIBLE
                        binding.nominalText.text = nominal
                        tspeech(nominal)
                        delay(2000)
                        binding.notificationResult.visibility = View.GONE
                        onProcess = false
                    }
                }

                is Result.Error -> {
                    photoFile?.delete()
                    onProcess = false
                    tspeech("Gagal Mengambil data, Coba lagi")
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), "Gagal Mengambil Gambar", Toast.LENGTH_SHORT)
                        .show()
                }
                is Result.Loading -> {
                    onProcess = true
                    binding.progressIndicator.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun tspeech(message: String) {
        if (!::tts.isInitialized) {  // Inisialisasi tts hanya jika belum ada
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
            if (!onProcess) {
                shutterOn()
            } else {
                tspeech("Sedang Melakukan Scan, Harap tunggu")
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
                Toast.makeText(requireContext(), "Gagal Memunculkan Kamera", Toast.LENGTH_SHORT).show()
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

    }

    companion object {
        private const val TAG = "CameraActivity"
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
