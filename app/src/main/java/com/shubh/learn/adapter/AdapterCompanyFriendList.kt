package com.shubh.learn.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shubh.learn.R
import com.shubh.learn.databinding.ItemManagerBinding
import com.shubh.learn.dto.UserDto
import com.shubh.learn.utills.OnFriendItemClickListener
import com.shubh.learn.utills.SharedPref
import com.vilborgtower.user.utils.Constant

class AdapterCompanyFriendList(
    val mContext: Context,
    var transList: ArrayList<UserDto>, val listener: OnFriendItemClickListener
) : RecyclerView.Adapter<AdapterCompanyFriendList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemManagerBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_manager, parent, false
        )
        sharedPref = SharedPref(mContext)
//        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {
        val data: UserDto = transList[position]
        "${data.first_name} ${data.last_name}".also { holder.binding.managerName.text = it }
        holder.binding.managerDetails.text = data.status
        /*Glide.with(mContext).load(data.image)
            .error(R.drawable.child_icon)
            .placeholder(R.drawable.child_icon)
            .into(holder.binding.ivCar)*/

        holder.binding.seeDetails.setOnClickListener {
            listener.onItemClick(data,"","1")
        }
        if (sharedPref.getStringValue(Constant.USER_TYPE).toString()
                .equals("Administrator", true)
        ) {
            if (data.type.toString() == "Company") {

holder.binding.editLay.visibility=View.VISIBLE
holder.binding.totalCoins.setText(data.rewards_point.toString())
            }else{
                holder.binding.editLay.visibility=View.GONE

            }
        }
        holder.binding.editCoin.setOnClickListener{
listener.onItemClick(data,holder.binding.totalCoins.text.toString(),"2")
        }
        Log.e(
            "TAG",
            "onBindViewHolder: type  " + sharedPref.getStringValue(Constant.USER_TYPE).toString()
        )
        Log.e("TAG", "onBindViewHolder:   type" + data.type)
    }

    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<UserDto>) {
        transList = filterlist
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return transList.size
    }

    class TransViewHolder(var binding: ItemManagerBinding) :
        RecyclerView.ViewHolder(binding.root)

}