package com.shubh.learn.ui.loginsignup

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.shubh.learn.R
import com.shubh.learn.databinding.FragmentSplashBinding
import com.shubh.learn.utills.SharedPref
import com.vilborgtower.user.utils.Constant
import java.util.*


class SplashFragment : Fragment() {
    lateinit var binding: FragmentSplashBinding
    lateinit var sharedPref: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_splash, container,
            false
        )

        sharedPref = SharedPref(requireContext())

        return binding.root
    }

    override fun onResume() {
        sharedPref = SharedPref(requireContext())
        setLocale(sharedPref.getStringValue(Constant.LANGUAGE))
        handlerMethod()
        super.onResume()
    }

    fun setLocale(lang: String?) {
        if (lang == "") {

            val myLocale = Locale("en")
            val res = resources
            val conf = res.configuration
            conf.locale = myLocale
            res.updateConfiguration(conf, res.displayMetrics)
        } else {

            val myLocale = Locale(lang)
            val res = resources
            val conf = res.configuration
            conf.locale = myLocale
            res.updateConfiguration(conf, res.displayMetrics)
        }

    }

    private fun handlerMethod() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (sharedPref.getBooleanValue(Constant.IS_LOGIN)) {
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_splash_to__compenyHomeFragment)
                /*   if (sharedPref.getStringValue(Constant.USER_TYPE)
                           .equals("Administrator", true)
                   ) {


                   } else {

                   }*/


            } else {
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_splash_to_login_type)

            }
        }, 3000)

    }

}