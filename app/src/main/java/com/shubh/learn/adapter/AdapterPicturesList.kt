package com.shubh.learn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shubh.learn.R
import com.shubh.learn.databinding.ItemPictureBinding
import com.shubh.learn.dto.PicturesListDto
import com.shubh.learn.utills.SharedPref
import com.shubh.learn.utills.onPictureItemClick

class AdapterPicturesList(
    val mContext: Context,
    var transList: ArrayList<PicturesListDto.Result>, val listener: onPictureItemClick
) : RecyclerView.Adapter<AdapterPicturesList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        val binding: ItemPictureBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_picture, parent, false)
        sharedPref = SharedPref(mContext)
//        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {

        val data: PicturesListDto.Result = transList[position]
        Glide.with(mContext)
            .load(data.image)
            .into(holder.binding.managerImage)
        holder.binding.seeDetails.setOnClickListener {
            listener.onItemClick( data)
        }
    }

    override fun getItemCount(): Int {
        return transList.size
    }
    class TransViewHolder(var binding: ItemPictureBinding) :
        RecyclerView.ViewHolder(binding.root)

}