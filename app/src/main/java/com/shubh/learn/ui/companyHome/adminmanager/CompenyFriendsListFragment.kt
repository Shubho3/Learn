package com.shubh.learn.ui.companyHome.adminmanager

import android.annotation.SuppressLint
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
import com.shubh.learn.adapter.AdapterCompanyFriendList
import com.shubh.learn.databinding.FragmentCompenyFriendsListBinding
import com.shubh.learn.dto.LoginDto
import com.shubh.learn.dto.ManagerListDto
import com.shubh.learn.dto.UserDto
import com.shubh.learn.retrofit.ApiClient
import com.shubh.learn.retrofit.ProviderInterface
import com.shubh.learn.utills.*
import com.vilborgtower.user.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CompenyFriendsListFragment : Fragment(), OnFriendItemClickListener {

    private lateinit var binding: FragmentCompenyFriendsListBinding
    lateinit var sharedPref: SharedPref
    lateinit var apiInterface: ProviderInterface
    var managerListDto: ArrayList<UserDto> = ArrayList()
    var managerListDtoFiltered: ArrayList<UserDto> = ArrayList()
    lateinit var adapterRideOption: AdapterCompanyFriendList
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_compeny_friends_list,
            container, false
        )
        sharedPref = SharedPref(requireContext())
        apiInterface = ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        binding.header.tvLogo.text = getString(R.string.comp_firnss)
        binding.btnAdd.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyFriendsListFragment_nav_to_compenyAddFriendsFragment)
        }
        binding.seeDetails.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_compenyFriendsListFragment_nav_to_compenyFriendsDetailsFragment)
        }
        binding.header.imgHeader.setOnClickListener {
            requireActivity().onBackPressed()
        }
        adapterRideOption =
            AdapterCompanyFriendList(requireActivity(), managerListDto, this@CompenyFriendsListFragment)
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

    override fun onResume() {
        getmanagerlist()
        super.onResume()
    }

    private fun filter(text: String) {
        val filteredlist: ArrayList<UserDto> = ArrayList()
        for (item in managerListDto) {
            if ((item.first_name+" "+item.last_name+" "+item.object_name+" "+item.town).lowercase(Locale.ROOT).contains(text.lowercase(Locale.ROOT))) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.no_data), Toast.LENGTH_SHORT).show()
        } else {
            adapterRideOption.filterList(filteredlist)
        }
    }

    private fun getmanagerlist() {
        managerListDto.clear()
        managerListDtoFiltered.clear()
        binding.managerList.showShimmerAdapter()
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        map["token"] = auth_token
        map["user_id"] = userid
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        apiInterface.get_companyList(map).enqueue(object : Callback<ManagerListDto?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ManagerListDto?>,
                response: Response<ManagerListDto?>
            ) {
                binding.managerList.hideShimmerAdapter()
                try {
                    val res: ManagerListDto = response.body()!!
                    if (response.body() != null && response.body()!!.status == "1") {
                        //     binding.root.successSnack(res.message)
                        managerListDto = res.result
                        if (managerListDto.size >= 1) {
                            binding.noData.visibility = View.GONE
                            adapterRideOption = AdapterCompanyFriendList(
                                requireActivity(),
                                managerListDto,
                                this@CompenyFriendsListFragment
                            )
                            binding.managerList.adapter = adapterRideOption
                        } else {
                            binding.noData.visibility = View.VISIBLE
                        }

                    } else {
                        binding.root.errorSnack(res.message)
                        binding.noData.visibility = View.VISIBLE

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    binding.noData.visibility = View.VISIBLE
                    Timber.tag("Exception").e("Exception = %s", e.message)
                }
            }

            override fun onFailure(call: Call<ManagerListDto?>, t: Throwable) {
                binding.managerList.hideShimmerAdapter()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
                binding.noData.visibility = View.VISIBLE


            }

        })

    }


    override fun onItemClick(model: UserDto, points: String, type: String) {
        if(type=="1"){
        Log.e("TAG", "onItemClick: " + model.toString())
        val bundle = Bundle()
        bundle.putSerializable("Result", model)
        Navigation.findNavController(binding.root).navigate(
            R.id.action_compenyFriendsListFragment_nav_to_compenyFriendsDetailsFragment,
            bundle
        )

    }else{
            update_wallet_point(points,model.id)
    }
    }
    private fun update_wallet_point(toString: String,friendID:String) {
        DataManager.instance.showProgressMessage(requireActivity(), getString(R.string.getting_user))
        val userid = sharedPref.getStringValue(Constant.USER_ID).toString()
        val auth_token = sharedPref.getStringValue(Constant.AUTH_TOKEN).toString()
        val map = HashMap<String, String>()
        map["user_id"] = friendID
        map["rewards_point"] = toString
        Timber.tag(ContentValues.TAG).e("Login user Request = %s", map)
        var apiInterface: ProviderInterface =
            ApiClient.getClient(requireContext())!!.create(ProviderInterface::class.java)
        apiInterface.update_wallet_point(map).enqueue(object : Callback<LoginDto?> {
            override fun onResponse(
                call: Call<LoginDto?>,
                response: Response<LoginDto?>
            ) {
                DataManager.instance.hideProgressMessage()
                try {
                    if (response.body()?.status == "1") {
                        //     binding.root.successSnack(res.message)
                        getmanagerlist()
                    } else {
                        binding.root.errorSnack(response.body()?.message!!)

                    }
                } catch (e: Exception) {
                    DataManager.instance.hideProgressMessage()
                    Timber.tag("Exception").e("Exception = %s", e.message)
                    Timber.tag("Exception").e("Exception = %s", e.printStackTrace())
                    Timber.tag("Exception").e("Exception = %s", e.localizedMessage)
                }
            }

            override fun onFailure(call: Call<LoginDto?>, t: Throwable) {
                call.cancel()
                DataManager.instance.hideProgressMessage()
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.localizedMessage)
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.cause.toString())
                Timber.tag(ContentValues.TAG).e("onFailure: %s", t.message.toString())
                binding.root.errorSnack(t.message.toString())
            }

        })
    }

}