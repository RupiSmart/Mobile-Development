package com.dicoding.rupismart_app.ui.help

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.rupismart_app.R
import com.dicoding.rupismart_app.ViewModelFactory
import com.dicoding.rupismart_app.adapter.HelpAdapter
import com.dicoding.rupismart_app.databinding.FragmentHelpBinding
import com.dicoding.rupismart_app.ui.setting.SettingActivity
import com.dicoding.rupismart_app.data.Result
import java.util.Locale

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!
    private lateinit var tts: TextToSpeech
    private var panduan: String =""
    private val viewModel by viewModels<HelpViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()

        val adapter = HelpAdapter()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        viewModel.getAllHelp.observe(viewLifecycleOwner){result->
            when(result){
                is Result.Loading -> {binding.progressBar.visibility = View.VISIBLE}
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireActivity(), result.error, Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    panduan = ""
                    val categories = result.data.categories
                    for (category in categories) {
                        panduan+="${category.title},${category.text}"
                    }
                    tspeech(panduan)
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(result.data.categories).apply {
                        binding.rvHelp.setHasFixedSize(false)
                        binding.rvHelp.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvHelp.adapter = adapter
                    }
                }

            }
        }
        binding.mainAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.setting -> {
                    startActivity(Intent(requireContext(), SettingActivity::class.java))
                   tts.stop()
                    true
                }
                else -> false
            }
        }
    }

    private fun playAnimation(){
        val appBar = ObjectAnimator.ofFloat(binding.appBarLayout, View.ALPHA, 1F).setDuration(400)
        val rvHistory = ObjectAnimator.ofFloat(binding.rvHelp, View.ALPHA, 1F).setDuration(400)
        AnimatorSet().apply {
            playSequentially(appBar, rvHistory)
            startDelay = 500
            start()

        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        if (::tts.isInitialized) {
            tts.stop()
        }
        _binding = null
    }
}