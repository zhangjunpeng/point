package com.zjdx.point.ui.tagging

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zjdx.point.databinding.ItemHisTagRecylerItemBinding
import com.zjdx.point.db.model.TagRecord
import com.zjdx.point.event.DeleteEvent
import com.zjdx.point.event.HisTagEvent
import com.zjdx.point.utils.PopWindowUtil
import org.greenrobot.eventbus.EventBus

class HisTagItemAdapter(val context: Context, val dataList: List<TagRecord>) :
    RecyclerView.Adapter<HisTagItemAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHisTagRecylerItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): HisTagItemAdapter.ViewHolder {

        val binding =
            ItemHisTagRecylerItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: HisTagItemAdapter.ViewHolder, position: Int) {

        val tagRecord = dataList[position]
        holder.binding.index.text = (position + 1).toString()

        val starArr = tagRecord.startTime.split(" ")
        val entArr = tagRecord.endTime.split(" ")
        holder.binding.info.text =
            "${tagRecord.destination}   ${starArr[0]}\n起止地点：${tagRecord.startType}-${tagRecord.endType}" + "\n起止时刻：${
                starArr.last().substring(
                    0,
                    4
                )
            }-${
                entArr.last().substring(
                    0,
                    4
                )
            }"

        holder.binding.check.setOnClickListener {
            EventBus.getDefault().postSticky(
                HisTagEvent(tagRecord)
            )
            context.startActivity(Intent(context, HisDeatailActivity::class.java))
        }
        holder.binding.delete.setOnClickListener {
            PopWindowUtil.instance.showDelDialog(context){
                EventBus.getDefault().post(DeleteEvent(tagRecord.id))
            }
        }

    }


    override fun getItemCount(): Int {
        return dataList.size
    }
}