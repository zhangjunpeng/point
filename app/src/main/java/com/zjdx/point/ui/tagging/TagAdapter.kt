package com.zjdx.point.ui.tagging

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zjdx.point.R
import com.zjdx.point.databinding.ItemTagRecylerBinding
import com.zjdx.point.event.EditTagEvent
import com.zjdx.point.utils.PopWindowUtil
import org.greenrobot.eventbus.EventBus

class TagAdapter(val context: Context, val viewModel: TaggingViewModel) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class ViewHolder(val binding: ItemTagRecylerBinding) : RecyclerView.ViewHolder(binding.root)

    inner class AddDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val view =
                LayoutInflater.from(context).inflate(R.layout.item_add_new_travel, parent, false)
            AddDataViewHolder(view)
        } else {
            val binding = ItemTagRecylerBinding.inflate(LayoutInflater.from(context),parent,false)
            ViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddDataViewHolder) {
            holder.itemView.setOnClickListener {
                viewModel.addTag.postValue(true)
            }
        } else if (holder is ViewHolder) {
            val tagRecord = viewModel.notUpTagRecord.value!![position ]
            holder.binding.index.text = (position + 1).toString()

            val starArr = tagRecord.startTime.split(" ")
            val entArr = tagRecord.endTime.split(" ")
            holder.binding.info.text =
                "${tagRecord.destination}   ${starArr[0]}\n起止地点：${tagRecord.startType}-${tagRecord.endType}" + "\n起止时刻：${
                    starArr[1].substring(
                        0,
                        4
                    )
                }-${entArr[1].substring(0, 4)}"

            holder.binding.up.setOnClickListener {
                if (position > 0) {
                    val tempList = viewModel.notUpTagRecord.value!!
                    val record = tempList.removeAt(position)
                    tempList.add(position - 1, record)
                    viewModel.notUpTagRecord.value = tempList
                    viewModel.hasChange = true
                }
            }

            holder.binding.down.setOnClickListener {
                if (position < viewModel.notUpTagRecord.value!!.size - 1) {
                    val tempList = viewModel.notUpTagRecord.value!!
                    val record = tempList.removeAt(position)
                    tempList.add(position + 1, record)
                    viewModel.notUpTagRecord.value = tempList
                    viewModel.hasChange = true
                }
            }
            holder.binding.edit.setOnClickListener {
                viewModel.hasChange = true
                viewModel.addingTag = tagRecord
                EventBus.getDefault().post(EditTagEvent())
            }

            holder.binding.del.setOnClickListener {
                PopWindowUtil.instance.showDelDialog(context) {
                    val tempList = viewModel.notUpTagRecord.value!!
                    val tag = tempList.removeAt(position)
                    viewModel.deleList.add(tag)
                    viewModel.notUpTagRecord.value = tempList
                    viewModel.hasChange = true

                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == viewModel.notUpTagRecord.value!!.size) {
            1
        } else {
            0
        }
    }

    override fun getItemCount(): Int {
        return viewModel.notUpTagRecord.value!!.size + 1
    }
}