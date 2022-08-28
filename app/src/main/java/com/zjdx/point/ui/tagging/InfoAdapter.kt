package com.zjdx.point.ui.tagging

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zjdx.point.databinding.ItemTagAddTrackBinding

class InfoAdapter(val context: Context, val dataList: ArrayList<String>) :
    RecyclerView.Adapter<InfoAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTagAddTrackBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTagAddTrackBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.add.setOnClickListener {
            dataList.add("")
            notifyDataSetChanged()
        }
        holder.binding.del.setOnClickListener {
            dataList.removeAt(position)
            notifyDataSetChanged()
        }
        holder.binding.up.setOnClickListener {
            if (position>0){
                val str=dataList[position]
                dataList.removeAt(position)
                dataList.add(position-1,str)
            }
            notifyDataSetChanged()
        }
        holder.binding.down.setOnClickListener {
            if (position<dataList.size-1){
                val str=dataList[position]
                dataList.removeAt(position)
                dataList.add(position+1,str)
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}