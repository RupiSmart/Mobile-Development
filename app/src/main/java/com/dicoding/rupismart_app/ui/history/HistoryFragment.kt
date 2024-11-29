package com.dicoding.rupismart_app.ui.history

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

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        val adapter = historyAdapter()
        viewModel.getAllHistory.observe(viewLifecycleOwner){result->
            when(result){
                is Result.Loading -> {}
                is Result.Error -> {
                    Toast.makeText(requireActivity(), result.error, Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    adapter.submitList(result.data.history).apply {
                        binding.rvHistory.setHasFixedSize(true)
                        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}