package com.shubh.learn.ui.companyHome.friend

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.shubh.learn.R
import com.shubh.learn.databinding.FragmentCompenyPointsBinding
import com.shubh.learn.dto.LoginDto
import com.shubh.learn.retrofit.ApiClient
import com.shubh.learn.retrofit.ProviderInterface
import com.shubh.learn.utills.DataManager
import com.shubh.learn.utills.SharedPref
import com.shubh.learn.utills.errorSnack
import com.vilborgtower.user.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class CompenyPointsFragment : Fragment() {

    private lateinit var binding: FragmentCompenyPointsBinding
    lateinit var sharedPref: SharedPref
    lateinit var apiInterface: ProviderInterface
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_compeny_points,
            container, false
        )
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)

        binding.header.tvLogo.text = getString(R.string.points)
        binding.header.imgHeader.setOnClickListener {
            requireActivity().onBackPressed()
        }
        getProfile()
        return binding.root
    }
    private fun getProfile() {
        DataManager.instance.showProgressMessage(
            requireActivity(),
            getString(R.string.getting_user)
        )
         val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
          map["token"] = auth_token
        map["user_id"] = userid
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
binding.tvCoins.text = " ${res.result.rewards_point}"
                        /*if (res.result.pr_qrcode_status=="Defualt"){
                            binding.tvCoins2.text  ="05"
                        }else{*/
                            binding.tvCoins2.text = " ${res.result.pr_qrcode_point}"
                        //}

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