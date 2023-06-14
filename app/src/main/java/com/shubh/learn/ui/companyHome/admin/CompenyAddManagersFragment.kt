package com.shubh.learn.ui.companyHome.admin

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.shubh.learn.R
import com.shubh.learn.databinding.FragmentCompenyAddManagersBinding
import com.shubh.learn.dto.LoginDto
import com.shubh.learn.utills.DataManager
import com.shubh.learn.utills.SharedPref
import com.shubh.learn.utills.Utils
import com.shubh.learn.utills.errorSnack
import com.shubh.learn.retrofit.ApiClient
import com.shubh.learn.retrofit.ProviderInterface
import com.shubh.learn.utills.*
import com.shubh.learn.utills.EmailValidation.isEmailValid
import com.vilborgtower.user.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
class CompenyAddManagersFragment : Fragment() {

    private lateinit var binding: FragmentCompenyAddManagersBinding
    lateinit var sharedPref: SharedPref
    lateinit var apiInterface: ProviderInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_compeny_add_managers,
            container, false
        )
        sharedPref = SharedPref(requireContext())
        binding.header.tvLogo.text = getString(R.string.add_manager)
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        /* binding.btnAdd.setOnClickListener {
              Navigation.findNavController(binding.root).
              navigate(R.id.action_compenyHomeFragment_nav_to_compenyFriendsListFragment)
         }*/
        binding.header.imgHeader.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnAdd.setOnClickListener {
          //  requireActivity().onBackPressed()
            val email     = binding.edtEmail.text.toString()
            val firstname = binding.firstName.text.toString()
            val lastname  = binding.lastName.text.toString()
            val pass      = binding.edtPass.text.toString()
            val no        = binding.editNo.text.toString()
            val code      = binding.ccp.selectedCountryCode.toString()
            if (firstname == "") binding.firstName.error=(getString(R.string.empty))
            if (lastname  == "") binding.lastName.error=(getString(R.string.empty))
            if (email     == ""&&! isEmailValid(email)) binding.edtEmail.error=(getString(R.string.empty))
            if (pass      == "") binding.edtPass.error=(getString(R.string.empty))
            //else if (c_code == "") binding.ccp.errorSnack(getString(R.string.empty))
            else if (no == "") binding.editNo.error=(getString(R.string.empty))
            else if (Utils.hasInternetConnection(requireActivity())) addManager(email, firstname, lastname, no, code ,pass  )
            else binding.root.errorSnack(getString(R.string.no_internet_connection))

        }

        return binding.root
    }

    private fun addManager(email: String,
                           firstname: String,
                           lastname: String, no: String,
                           cCode: String,pass: String)
        { DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.getting_user))
            val userid =  sharedPref.getStringValue(Constant.USER_ID).toString()
            val auth_token =  sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
            val map = HashMap<String, String>()
            map["email"] = email
            map["first_name"] = firstname
            map["last_name"] = lastname
            map["mobile"] = no
            map["country"] = cCode
            map["register_id"] = "123"
            map["password"] = pass
            map["token"] = auth_token
            map["user_id"] =userid
            Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
            apiInterface.add_manager(map).enqueue(object : Callback<LoginDto?> {
                override fun onResponse(call: Call<LoginDto?>, response: Response<LoginDto?>) {
                    DataManager.instance.hideProgressMessage()
                    try {
                        val res: LoginDto = response.body()!!
                        if (response.body() != null && response.body()!!.status == "1") {
                            binding.root.successSnack(res.message)
                           requireActivity().onBackPressed()
                        } else {
                            binding.root.errorSnack(res.message)
                        }
                    } catch (e: Exception) {
                        DataManager.instance.hideProgressMessage()
                        Toast.makeText(
                            context,
                            "Exception = " + e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        Timber.tag("Exception").e("Exception = %s", e.message)
                    }
                }

                override fun onFailure(call: Call<LoginDto?>, t: Throwable) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                    Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                    binding.root.errorSnack(t.message.toString())

                }

            })

        }


}