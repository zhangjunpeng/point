package com.zjdx.point.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zjdx.point.databinding.ItemRecylerMainBinding

class MainRecylerAdapter(val context: Context, val msgList: ArrayList<String>) :
    RecyclerView.Adapter<MainRecylerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    lateinit var binding: ItemRecylerMainBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemRecylerMainBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binding.msgItemRecylerMain.text = msgList[position]
    }

    override fun getItemCount(): Int {
        return msgList.size
    }
}