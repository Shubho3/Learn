package com.shubh.learn.ui.loginsignup

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.shubh.learn.R
import com.shubh.learn.databinding.FragmentSignupAdminBinding
import com.shubh.learn.dto.LoginDto
import com.shubh.learn.retrofit.ApiClient
import com.shubh.learn.retrofit.ProviderInterface
import com.shubh.learn.utills.*
import com.vilborgtower.user.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class SignupAdminFragment : Fragment() {
    private lateinit var binding: FragmentSignupAdminBinding
    lateinit var sharedPref: SharedPref
    lateinit var apiInterface: ProviderInterface
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_signup_admin,
            container, false
        )
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        binding.btnSubmit.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val firstname = binding.firstName.text.toString()
            val lastname  = binding.lastName.text.toString()
            val no       = binding.editNo.text.toString()
            val c_code    = binding.ccp.selectedCountryCode.toString()
            val pass    = binding.edtPass.text.toString()
            if (firstname == "") binding.firstName.error=(getString(R.string.empty))
            if (lastname == "") binding.lastName.error=(getString(R.string.empty))
            if (email == "") binding.edtEmail.error=(getString(R.string.empty))
            else if (pass == "") binding.edtPass.error=(getString(R.string.empty))
            //else if (c_code == "") binding.ccp.errorSnack(getString(R.string.empty))
            else if (no == "") binding.editNo.error=(getString(R.string.empty))
            else //if (Utils.hasInternetConnection(requireActivity()))
                signupApi(email, pass, firstname, lastname, no, c_code   )
           // else binding.root.errorSnack(getString(R.string.no_internet_connection))

        }
        return binding.root
    }

    private fun signupApi(
        email: String,
        pass: String,
        firstname: String,
        lastname: String,
        no: String,
        c_code: String
    ) {
        DataManager.instance.showProgressMessage(
            requireActivity(),
            getString(R.string.getting_user)
        )
        val map = HashMap<String, String>()
        map["email"] = email
        map["password"] = pass
        map["first_name"] = firstname
        map["last_name"] = lastname
        map["mobile"] = no
        map["country"] = c_code
        map["register_id"] = "123"
        //   map["register_id"] = sharedPref.getStringValue(Constant.FIREBASETOKEN).toString()
        map["type"] = "Administrator"
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.signup(map).enqueue(object : Callback<LoginDto?> {
            override fun onResponse(
                call: Call<LoginDto?>,
                response: Response<LoginDto?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    val res: LoginDto = response.body()!!

                    if (response.body() != null && response.body()!!.status == "1") {
                        binding.root.successSnack(res.message)
                        sharedPref.setStringValue(Constant.USER_ID, res.result.id)
                        sharedPref.setStringValue(Constant.USER_TYPE, "Administrator")
                        sharedPref.setStringValue(Constant.AUTH_TOKEN, res.result.token)
                        sharedPref.setBooleanValue(Constant.IS_LOGIN, true)
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_loginAdminFragment_to_compenyHomeFragment)

                    } else {
                        binding.root.errorSnack(res.message)
                    }


                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                       
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