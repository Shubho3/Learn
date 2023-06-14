package com.shubh.learn.ui.companyHome.admin

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.shubh.learn.R
import com.shubh.learn.adapter.AdapterManagersList
import com.shubh.learn.databinding.FragmentCompenyManagerListBinding
import com.shubh.learn.dto.ManagerListDto
import com.shubh.learn.dto.UserDto
import com.shubh.learn.retrofit.ApiClient
import com.shubh.learn.retrofit.ProviderInterface
import com.shubh.learn.utills.DataManager
import com.shubh.learn.utills.OnItemClickListener
import com.shubh.learn.utills.SharedPref
import com.shubh.learn.utills.errorSnack
import com.vilborgtower.user.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CompenyManagerListFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentCompenyManagerListBinding
    lateinit var sharedPref: SharedPref
    lateinit var apiInterface: ProviderInterface
    var managerListDto: ArrayList<UserDto> = ArrayList()
    lateinit var adapterRideOption: AdapterManagersList
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_compeny_manager_list,
            container, false
        )
        binding.header.tvLogo.text = getString(R.string.manager_list)
        binding.btnAdd.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyManagerListFragment_to_compenyAddManagersFragment)
        }
        binding.seeDetails.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyManagerListFragment_to_compenyManagerDetailsFragment)
        }
        binding.header.imgHeader.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.edtSearchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //  Toast.makeText(requireContext(),s,Toast.LENGTH_SHORT).show()
                filter(s.toString())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }
        })
        return binding.root
    }
    private fun filter(text: String) {
        val filteredlist: ArrayList<UserDto> = ArrayList()
        for (item in managerListDto) {
            if ((item.first_name+" "+item.last_name).lowercase(Locale.ROOT).contains(text.lowercase(
                    Locale.ROOT))) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.no_data), Toast.LENGTH_SHORT).show()
        } else {
            adapterRideOption.filterList(filteredlist)
        }
    }

    override fun onResume() {
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        getmanagerlist()
       // binding.managerList.hideShimmerAdapter()
        super.onResume()
    }

    private fun getmanagerlist() {
        binding.managerList.showShimmerAdapter()
        // managerListDto.clear()
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        map["token"] = auth_token
        map["user_id"] = userid
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_managerList(map).enqueue(object : Callback<ManagerListDto?> {
            override fun onResponse(
                call: Call<ManagerListDto?>,
                response: Response<ManagerListDto?>
            ) {
                binding.managerList.hideShimmerAdapter()
                try {
                    val res: ManagerListDto = response.body()!!
                    if (response.body() != null && response.body()!!.status == "1") {
                        //     binding.root.successSnack(res.message)
                         managerListDto= res.result
                         if (managerListDto.size>=1) {
                             binding.noData.visibility= View.GONE
                              adapterRideOption = AdapterManagersList(
                                 requireActivity(),
                                 managerListDto, this@CompenyManagerListFragment)
                             binding.managerList.adapter = adapterRideOption
                             binding.managerList.setHasFixedSize(true)
                         }else{
                             binding.noData.visibility= View.VISIBLE
                         }

                    } else {
                        binding.root.errorSnack(res.message)
                        binding.noData.visibility= View.VISIBLE

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    binding.noData.visibility= View.VISIBLE
                       
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<ManagerListDto?>, t: Throwable) {
                binding.managerList.hideShimmerAdapter()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
                binding.noData.visibility= View.VISIBLE


            }

        })

    }

    override fun onItemClick(model: UserDto) {
        val bundle = Bundle()
        bundle.putSerializable("Result", model)
        Log.e("TAG", "onItemClick: "+model.toString())
     //   bundle.putParcelable("data", userDto)
     Navigation.findNavController(binding.root).
       navigate(R.id.action_compenyManagerListFragment_to_compenyManagerDetailsFragment,bundle)

    }

}