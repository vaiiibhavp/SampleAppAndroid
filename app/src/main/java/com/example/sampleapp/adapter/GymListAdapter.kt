package com.example.sampleapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sampleapp.Utils.MyShare
import com.example.sampleapp.Utils.UtilityClass
import com.example.sampleapp.databinding.ItemGymListLayoutBinding
import com.example.sampleapp.modal.gymListResponce.GymListData


class GymListAdapter(
    var context: Context,
    val gymArrayList: ArrayList<GymListData>,
    val listener: onClickListener?
) : RecyclerView.Adapter<GymListAdapter.GymListViewHolder>() {

    class GymListViewHolder(val binding: ItemGymListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymListViewHolder {
        return GymListViewHolder(
            ItemGymListLayoutBinding.inflate(
                LayoutInflater.from(
                    context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: GymListViewHolder, listPosition: Int) {

        try {
            val data = gymArrayList.get(listPosition)

            holder.binding.textViewName.text = data.Name
            holder.binding.txtAddress.text = data.Address
            Glide.with(context).load(MyShare(context).gymListBaseImageUrl + data.Label).into(holder.binding.imageView);
            val distnce = UtilityClass.getLatLongDistance(context, data.Lat!!, data.Long!!)
            holder.binding.txtkm.text = distnce.toString() + " Km"

            holder.binding.llItemMainView.setOnClickListener {
                listener?.onViewItemClick(data.ID.toString())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        return gymArrayList.size
    }

    interface onClickListener {
        fun onViewItemClick(gymId: String)
    }
}