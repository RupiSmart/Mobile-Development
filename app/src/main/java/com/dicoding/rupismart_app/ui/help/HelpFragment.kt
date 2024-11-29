package com.dicoding.rupismart_app.ui.help

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.dicoding.rupismart_app.R
import com.dicoding.rupismart_app.ViewModelFactory
import com.dicoding.rupismart_app.databinding.FragmentHelpBinding
import com.dicoding.rupismart_app.ui.scan.ScanViewModel
import com.dicoding.rupismart_app.ui.setting.SettingActivity

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

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

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
//Test
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}