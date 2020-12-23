package com.zjdx.point.ui.travel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zjdx.point.databinding.ItemRecylerTravelAcBinding
import com.zjdx.point.db.model.Location

class TravelRecylerAdapter(val context: Context) :
    ListAdapter<Location, TravelRecylerAdapter.ViewHolder>(LocationComparator()) {


    lateinit var binding: ItemRecylerTravelAcBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemRecylerTravelAcBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        binding.addressItemRecylerTravelAc.text = "位置信息:" + current.address
        binding.latItemRecylerTravelAc.text = "经度:" + current.lat.toString()
        binding.lngItemRecylerTravelAc.text = "纬度:" + current.lng.toString()
        binding.timeItemRecylerTravelAc.text = "时间：" + current.creatTime

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    class LocationComparator : DiffUtil.ItemCallback<Location>() {
        override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem == newItem
        }
    }
}