package com.shubh.learn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shubh.learn.R
import com.shubh.learn.databinding.ItemPointsBinding
import com.shubh.learn.dto.PointsDto
import com.shubh.learn.utills.SharedPref
import com.shubh.learn.utills.onRewardItemClick

class AdapterRewardsList(
    val mContext: Context,
    var transList: ArrayList<PointsDto.Result>, val listener: onRewardItemClick
) : RecyclerView.Adapter<AdapterRewardsList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemPointsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_points, parent, false
        )
        sharedPref = SharedPref(mContext)
//        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {

        val data: PointsDto.Result = transList[position]
        "${data.other_first_name} ${data.other_last_name}".also { holder.binding.name.text = it }
        holder.binding.details.text = data.point+" "+ mContext.getString(R.string.reward_points_added_successfully)
        holder.binding.date.text = data.date_time
        /*Glide.with(mContext).load(data.image)
            .error(R.drawable.child_icon)
            .placeholder(R.drawable.child_icon)
            .into(holder.binding.ivCar)*/

        holder.binding.root.setOnClickListener {
            listener.onItemClick( data)
        }
    }

    override fun getItemCount(): Int {
        return transList.size
    }
    class TransViewHolder(var binding: ItemPointsBinding) :
        RecyclerView.ViewHolder(binding.root)

}