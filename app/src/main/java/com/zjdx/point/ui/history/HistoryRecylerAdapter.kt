package com.zjdx.point.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zjdx.point.databinding.ItemRecylerHistoryAcBinding
import com.zjdx.point.db.model.TravelRecord

class HistoryRecylerAdapter(val context: Context, val travelRecordList: List<TravelRecord>) :
    RecyclerView.Adapter<HistoryRecylerAdapter.ViewHolder>() {

    lateinit var binding: ItemRecylerHistoryAcBinding


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemRecylerHistoryAcBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return travelRecordList.size
    }
}