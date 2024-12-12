package com.dicoding.rupismart_app.ui.history

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.dicoding.rupismart_app.adapter.historyAdapter
import com.dicoding.rupismart_app.databinding.FragmentHistoryBinding
import com.dicoding.rupismart_app.ui.setting.SettingActivity
import com.dicoding.rupismart_app.data.Result
import java.util.Locale

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playAnimation()

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        val adapter = historyAdapter()
        viewModel.getAllHistory.observe(viewLifecycleOwner){ result->
            when(result){
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE

                    Toast.makeText(requireActivity(), result.error, Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(result.data).apply {
                        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvHistory.setHasFixedSize(false)
                        binding.rvHistory.adapter = adapter
                    }
                }

            }
        }
        binding.mainAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.setting -> {
                    startActivity(Intent(requireContext(), SettingActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun playAnimation(){
        val appBar = ObjectAnimator.ofFloat(binding.appBarLayout, View.ALPHA, 1F).setDuration(400)
        val rvHistory = ObjectAnimator.ofFloat(binding.rvHistory, View.ALPHA, 1F).setDuration(400)
        AnimatorSet().apply {
            playSequentially(appBar, rvHistory)
            startDelay = 500
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}