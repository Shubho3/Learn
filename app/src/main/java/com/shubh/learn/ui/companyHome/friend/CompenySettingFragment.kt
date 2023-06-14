package com.shubh.learn.ui.companyHome.friend

import android.app.Dialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.shubh.learn.R
import com.shubh.learn.databinding.FragmentCompenySettingBinding
import com.shubh.learn.dto.AdminPointsDto
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

class CompenySettingFragment : Fragment() {

    private lateinit var binding: FragmentCompenySettingBinding
    lateinit var sharedPref: SharedPref
    lateinit var apiInterface: ProviderInterface
    lateinit var ide: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_compeny_setting,
            container, false
        )
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)

        binding.header.tvLogo.text = getString(R.string.setting)
        binding.header.imgHeader.setOnClickListener {
            requireActivity().onBackPressed()
        }
        getProfile()
         binding.editCoin.setOnClickListener{
             val dialog = Dialog(requireContext())
             dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
             dialog.window?.attributes?.windowAnimations =
                 android.R.style.Widget_Material_ListPopupWindow
             dialog.setContentView(R.layout.update_coin)
             val lp = WindowManager.LayoutParams()
             val window: Window = dialog.window!!
             lp.copyFrom(window.attributes)
             lp.width = WindowManager.LayoutParams.WRAP_CONTENT
             lp.height = WindowManager.LayoutParams.WRAP_CONTENT
             window.attributes = lp

             val edt_coin: EditText = dialog.findViewById(R.id.edt_coin)
             edt_coin.setText(binding.tvCoins.text.toString().trim())
             val yes_btn: TextView = dialog.findViewById(R.id.btn_yes)
             val no_btn: TextView = dialog.findViewById(R.id.btn_no)
             no_btn.setOnClickListener {
                 dialog.dismiss()
             }
             yes_btn.setOnClickListener {
                  var coin = edt_coin.text.toString()
                  if (coin.isEmpty()) edt_coin.error= getString(R.string.empty)
                 else{
                     updateCoin(coin)
                      dialog.dismiss()

                  }
               //  delete_child_api(model.id.toString())
             }
             dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
             dialog.show()
         }
        return binding.root
    }

    private fun updateCoin(coin: String)
    {
        DataManager.instance.showProgressMessage(
            requireActivity(),
            getString(R.string.getting_user)
        )
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
         map["token"] = auth_token
         map["user_id"] = userid
         map["point"] = coin
         map["rewards_id"] = ide
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.update_rewards(map).enqueue(object : Callback<AdminPointsDto?> {
            override fun onResponse(
                call: Call<AdminPointsDto?>,
                response: Response<AdminPointsDto?>
            ) {
                getProfile()
                try {

                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()

                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<AdminPointsDto?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
            }

        })

    }

    private fun getProfile() {
        DataManager.instance.showProgressMessage(
            requireActivity(),
            getString(R.string.getting_user)
        )
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
       // map["token"] = auth_token
       // map["user_id"] = userid
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_point(map).enqueue(object : Callback<AdminPointsDto?> {
            override fun onResponse(
                call: Call<AdminPointsDto?>,
                response: Response<AdminPointsDto?>
            ) {
                DataManager.instance.hideProgressMessage()

                try {
                    val res: AdminPointsDto = response.body()!!
                    if (response.body() != null && response.body()!!.status == "1") {
                        //     binding.root.successSnack(res.message)
                        binding.tvCoins.text = " ${res.result.point}"
                        ide = res.result.id.toString()
                    } else {
                        binding.root.errorSnack(res.message)

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()

                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<AdminPointsDto?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()

                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
            }

        })

    }

}