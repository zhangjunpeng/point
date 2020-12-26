package com.zjdx.point.ui.history

import android.content.Context
import android.content.Intent
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
        travelRecordList[position].run {
            binding.timeItemRecylerHistoryAc.text = createTime
            binding.traveltypeItemRecylerHistoryAc.text = travelTypes
            binding.isuploadItemRecylerHistoryAc.text = if (isUpload == 0) {
                "否"
            } else {
                "是"
            }
        }

        binding.checkLocationsItemRecylerHistoryAc.setOnClickListener {
            val intent=Intent(context,HistoryLocationActivity::class.java)
            intent.putExtra("tid",travelRecordList[position].id)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return travelRecordList.size
    }
}