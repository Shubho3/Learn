package com.shubh.learn.ui.companyHome.friend

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.shubh.learn.R
import com.shubh.learn.databinding.FragmentContactToCompanyBinding
import com.shubh.learn.dto.LoginDto
import com.shubh.learn.retrofit.ApiClient
import com.shubh.learn.retrofit.ProviderInterface
import com.shubh.learn.utills.DataManager
import com.shubh.learn.utills.SharedPref
import com.shubh.learn.utills.Utils
import com.shubh.learn.utills.errorSnack
import com.vilborgtower.user.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ContactToCompanyFragment : Fragment() {

    private lateinit var binding: FragmentContactToCompanyBinding
    lateinit var sharedPref: SharedPref
    lateinit var apiInterface: ProviderInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_contact_to_company,
            container, false
        )
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)

        binding.header.tvLogo.text = getString(R.string.contact_to_company)

       binding.header.imgHeader.setOnClickListener {
            requireActivity().onBackPressed()
        }
 binding.btnSubmit.setOnClickListener {
     val name = binding.name.text.toString()
     val email = binding.edtEmail.text.toString()
     val message = binding.edtMessage.text.toString()
     if (email == "") binding.edtEmail.errorSnack(getString(R.string.empty_fields))
     else if (name == "") binding.name.errorSnack(getString(R.string.empty_fields))
     else if (message == "") binding.edtMessage.errorSnack(getString(R.string.empty_fields))
     else if (Utils.hasInternetConnection(requireActivity())) contact_to_com(email, name,message)
     else binding.root.errorSnack(getString(R.string.no_internet_connection))

     requireActivity().onBackPressed()
        }

            binding.name.setOnClickListener {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:" + "+48603656744")
                startActivity(dialIntent)
        }
        binding.edtEmail.setOnClickListener {
               composeEmail("biuro@sebateks.pl","Contact to company")
        }

        binding.edtMessage.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.sebateks.pl"))
            startActivity(browserIntent)        }

        return binding.root
    }

    fun composeEmail(addresses: String, subject: String?) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }
    private fun contact_to_com(email: String, name: String, message: String) {
        DataManager.instance.showProgressMessage(
            requireActivity(),
            getString(R.string.getting_user)
        )
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        map["token"] = auth_token
        map["user_id"] = userid
        map["email"] = email
        map["name"] = name
        map["message"] = message
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_profile(map).enqueue(object : Callback<LoginDto?> {
            override fun onResponse(
                call: Call<LoginDto?>,
                response: Response<LoginDto?>
            ) {
                DataManager.instance.hideProgressMessage()

                try {
                    val res: LoginDto = response.body()!!
                    if (response.body() != null && response.body()!!.status == "1") {
                        //     binding.root.successSnack(res.message)


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