package com.example.sampleapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleapp.R


class GymTagAdapter(var context: Context, var mData: List<String>) :
    RecyclerView.Adapter<GymTagAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtAddress: TextView

        init {
            txtAddress = itemView.findViewById<View>(R.id.txtAddress) as TextView

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_gym_tag_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, listPosition: Int) {
        val animal= mData.get(listPosition)
        holder.txtAddress.text = animal

    }

    override fun getItemCount(): Int {
        return mData.size
    }
}