package com.shubh.learn.ui.loginsignup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.shubh.learn.R
import com.shubh.learn.databinding.BottemSheeetLanguageBinding
import com.shubh.learn.databinding.FragmentLoginTypeBinding
import com.shubh.learn.utills.SharedPref
import com.vilborgtower.user.utils.Constant
import java.util.*


class LoginTypeFragment : Fragment() {
    private lateinit var binding: FragmentLoginTypeBinding
    private val ENABLE_GPS = 3030
    private val REQUEST_LOCATION_PERMISSION = 1
    lateinit var sharedPref: SharedPref

    var mLocation: Location? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login_type,
            container, false
        )

        sharedPref = SharedPref(requireContext())

        binding.selectLanguage.setOnClickListener {
            openLanguageBottomSheet()

        }
        FirebaseApp.initializeApp(requireContext())
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result
                sharedPref.setStringValue(Constant.FIREBASETOKEN, token + "")
                Log.d("TAG", token)
            })
        customTextView(binding.prvc)
        binding.compenyRela.setOnClickListener {
            sharedPref.setStringValue(Constant.USER_TYPE, "Company")
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_login_type_nav_to_loginCompenyFragment)
        }

        binding.managerRela.setOnClickListener {
            sharedPref.setStringValue(Constant.USER_TYPE, "Manager")
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_login_type_nav_to_loginCompenyFragment)

        }

        binding.adminRela.setOnClickListener {
            sharedPref.setStringValue(Constant.USER_TYPE, "Administrator")
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_login_type_nav_to_lloginAdminFragment)

        }

        return binding.root
    }

    private fun customTextView(view: TextView) {
        val spanTxt = SpannableStringBuilder(getString(R.string.i_aggree))
        spanTxt.append(" " + getString(R.string.tos))
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(activity, "Terms of services Clicked", Toast.LENGTH_SHORT).show()
            }
        }, spanTxt.length - getString(R.string.tos).length, spanTxt.length, 0)
        spanTxt.append(" " + getString(R.string.and) + " ")
        spanTxt.setSpan(ForegroundColorSpan(Color.BLACK), 32, spanTxt.length, 0)
        spanTxt.append(getString(R.string.privacy))
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(activity, "Privacy Policy Clicked", Toast.LENGTH_SHORT).show()
            }
        }, spanTxt.length - getString(R.string.privacy).length, spanTxt.length, 0)
        view.movementMethod = LinkMovementMethod.getInstance()
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    private fun openLanguageBottomSheet() {
        val bottomSheetBinding: BottemSheeetLanguageBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(requireActivity()), R.layout.bottem_sheeet_language,
                null, false
            )
        val bottomSheetDialog = RoundedBottomSheetDialog(requireActivity())
        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
        if (sharedPref.getStringValue(Constant.LANGUAGE) == "pl") {
            bottomSheetBinding.pol.isChecked = true
        } else {
            bottomSheetBinding.eng.isChecked = true

        }
        bottomSheetBinding.englishRela.setOnClickListener {
            sharedPref.setStringValue(Constant.LANGUAGE, "en")
            val language = "en"
            setLocale(sharedPref.getStringValue(Constant.LANGUAGE))
            bottomSheetDialog.dismiss()
            activity?.recreate()
        }
        bottomSheetBinding.polishRela.setOnClickListener {
            sharedPref.setStringValue(Constant.LANGUAGE, "pl")
            val language = "pl"
            setLocale(sharedPref.getStringValue(Constant.LANGUAGE))

            bottomSheetDialog.dismiss()
            activity?.recreate()

        }
    }

    fun setLocale(lang: String?) {
        val myLocale = Locale(lang)
        val res = resources
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, res.displayMetrics)
    }


}