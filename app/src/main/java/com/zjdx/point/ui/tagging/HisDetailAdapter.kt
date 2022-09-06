package com.zjdx.point.ui.tagging

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zjdx.point.databinding.ItemSpinnerBaseWorkBinding

class HisDetailAdapter(val context: Context, val dataList: List<String>) :
    RecyclerView.Adapter<HisDetailAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ItemSpinnerBaseWorkBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HisDetailAdapter.ViewHolder {

        val binding =
            ItemSpinnerBaseWorkBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: HisDetailAdapter.ViewHolder, position: Int) {
        holder.binding.tvItemSpinnerBaseWork.text = dataList[position]
    }


    override fun getItemCount(): Int {
        return dataList.size
    }
}