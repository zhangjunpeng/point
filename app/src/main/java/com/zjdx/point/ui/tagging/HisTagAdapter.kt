package com.zjdx.point.ui.tagging

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zjdx.point.databinding.ItemHisTagRecylerBinding

class HisTagAdapter(val context: Context, val viewModel: HisTagViewModel) :
    RecyclerView.Adapter<HisTagAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ItemHisTagRecylerBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HisTagAdapter.ViewHolder {

        val binding = ItemHisTagRecylerBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: HisTagAdapter.ViewHolder, position: Int) {
        val key = viewModel.allTagLiveData.value!!.keys.toList()[position]
        holder.binding.date.text = "上传日期：$key"
        holder.binding.itemRecyler.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        holder.binding.itemRecyler.adapter =
            HisTagItemAdapter(context, viewModel.allTagLiveData.value!![key]!!)
    }


    override fun getItemCount(): Int {
        return viewModel.allTagLiveData.value!!.keys.size
    }
}