package com.dicoding.rupismart_app.ui.help

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.rupismart_app.R
import com.dicoding.rupismart_app.ViewModelFactory
import com.dicoding.rupismart_app.adapter.helpAdapter
import com.dicoding.rupismart_app.databinding.FragmentHelpBinding
import com.dicoding.rupismart_app.ui.scan.ScanViewModel
import com.dicoding.rupismart_app.ui.setting.SettingActivity
import com.dicoding.rupismart_app.data.Result
class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!
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
        val adapter = helpAdapter()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        viewModel.getAllHelp.observe(viewLifecycleOwner){result->
            when(result){
                is Result.Loading -> {}
                is Result.Error -> {
                    Toast.makeText(requireActivity(), result.error, Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    adapter.submitList(result.data.categories).apply {
                        binding.rvHelp.setHasFixedSize(true)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}