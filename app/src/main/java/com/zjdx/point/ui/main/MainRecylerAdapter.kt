package com.zjdx.point.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zjdx.point.R
import com.zjdx.point.databinding.ItemRecylerMainBinding

class MainRecylerAdapter(val context: Context, val msgList: ArrayList<String>) :
    RecyclerView.Adapter<MainRecylerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView=itemView.findViewById<TextView>(R.id.msg_item_recyler_main)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.item_recyler_main,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.textView.text=msgList[position]
    }

    override fun getItemCount(): Int {
        return msgList.size
    }
}