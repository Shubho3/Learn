package com.shubh.learn.ui.loginsignup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.shubh.learn.R
import com.shubh.learn.databinding.FragmentForgetPasswordBinding
import com.shubh.learn.utills.SharedPref

class ForgetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgetPasswordBinding
    lateinit var sharedPref: SharedPref
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_forget_password,
            container, false
        )
        sharedPref = SharedPref(requireContext())
        binding.header.tvLogo.visibility= View.GONE
        binding.header.imgHeader.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }
}