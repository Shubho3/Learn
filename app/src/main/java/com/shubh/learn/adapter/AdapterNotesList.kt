package com.shubh.learn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shubh.learn.R
import com.shubh.learn.databinding.ItemNotesBinding
import com.shubh.learn.dto.NotesDto
import com.shubh.learn.utills.OnNoteClickListener
import com.shubh.learn.utills.SharedPref

class AdapterNotesList(
    val mContext: Context,
    var transList: ArrayList<NotesDto.Result>, val listener: OnNoteClickListener
) : RecyclerView.Adapter<AdapterNotesList.TransViewHolder>() {

    lateinit var sharedPref: SharedPref

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        var binding: ItemNotesBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_notes, parent, false
        )
        sharedPref = SharedPref(mContext)
//        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS)
        return TransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {

        val data: NotesDto.Result = transList[position]
       " ${ mContext.getString(R.string.noteby) } ${data.first_name} ${data.last_name}".also { holder.binding.byName.text = it }
        holder.binding.topicName.text = data.topic
        holder.binding.desc.text = data.notes
        holder.binding.time.text = data.time_ago
        /*Glide.with(mContext).load(data.image)
            .error(R.drawable.child_icon)
            .placeholder(R.drawable.child_icon)
            .into(holder.binding.ivCar)*/

        holder.binding.btnDelete.setOnClickListener {
            listener.onItemClick( data)
        }
    }
    // method for filtering our recyclerview items.

    override fun getItemCount(): Int {
        return transList.size
    }
    class TransViewHolder(var binding: ItemNotesBinding) :
        RecyclerView.ViewHolder(binding.root)

}