package com.shubh.learn.ui.companyHome.admin

import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.shubh.learn.R
import com.shubh.learn.databinding.BottemSheeetAccessManagerBinding
import com.shubh.learn.databinding.FragmentCompenyManagerDetailsBinding
import com.shubh.learn.dto.ResponseData
import com.shubh.learn.dto.UserDto
import com.shubh.learn.retrofit.ApiClient
import com.shubh.learn.retrofit.ProviderInterface
import com.shubh.learn.utills.DataManager
import com.shubh.learn.utills.SharedPref
import com.shubh.learn.utills.errorSnack
import com.shubh.learn.utills.successSnack
import com.vilborgtower.user.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class CompenyManagerDetailsFragment : Fragment() {

    private lateinit var binding: FragmentCompenyManagerDetailsBinding
    lateinit var sharedPref: SharedPref
    var result:UserDto?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_compeny_manager_details,
            container, false
        )

        if (arguments != null) {
             result = requireArguments().getSerializable("Result") as UserDto?
            Log.e("getuserValidate", result.toString())
            binding.firstName.text = result?.first_name
            binding.lastName.text = result?.last_name
            binding.txtEmail.text = result?.email
            binding.txtPhoneNo.text = result?.mobile
            binding.friendBusines.text = result?.date_time
        }
        // val  bundle :UserDto = arguments?.getParcelable("data",UserDto::class.java)!!
        //  Log.e("TAG", "onCreateView: "+bundle)
        sharedPref = SharedPref(requireContext())
        binding.header.tvLogo.text = getString(R.string.manager_details)

        binding.header.imgHeader.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnSubmit.setOnClickListener {
            openEditBottomSheet()
        }
        return binding.root
    }

    private fun openEditBottomSheet() {
        val bottomSheetBinding: BottemSheeetAccessManagerBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(requireActivity()), R.layout.bottem_sheeet_access_manager,
                null, false
            )
        val bottomSheetDialog = RoundedBottomSheetDialog(requireActivity())
        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
        bottomSheetBinding.btnDelete.setOnClickListener {
            deleteManagerApi(result?.id.toString())
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.btnEdit.setOnClickListener {
            bottomSheetDialog.dismiss()

        }
    }

    private fun deleteManagerApi(id:String) {
        DataManager.instance.showProgressMessage(
            requireActivity(),
            getString(R.string.getting_user)
        )
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        map["token"] = auth_token
        map["user_id"] = userid
        map["id"] = id
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        var apiInterface: ProviderInterface =
            ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        apiInterface.delete_user(map).enqueue(object : Callback<ResponseData?> {
            override fun onResponse(
                call: Call<ResponseData?>,
                response: Response<ResponseData?>
            ) {
                DataManager.instance.hideProgressMessage()

                try {
                    val res: ResponseData = response.body()!!
                    if (response.body() != null && response.body()!!.status == "1") {
                        //     binding.root.successSnack(res.message)
                        binding.root.successSnack(res.result)

                        requireActivity().onBackPressed()
                    } else {
                        binding.root.errorSnack(res.message)

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                       
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<ResponseData?>, t: Throwable) {
                DataManager.instance.hideProgressMessage()

                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
            }

        })

    }

}