package com.zjdx.point.ui.tagging

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zjdx.point.R
import com.zjdx.point.databinding.ItemTagRecylerBinding
import com.zjdx.point.utils.DateUtil

class TagAdapter(val context: Context, val viewModel: TaggingViewModel) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class ViewHolder(val binding: ItemTagRecylerBinding) : RecyclerView.ViewHolder(binding.root)

    inner class AddDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view =
                LayoutInflater.from(context).inflate(R.layout.item_add_new_travel, parent, false)
            AddDataViewHolder(view)
        } else {
            val binding = ItemTagRecylerBinding.inflate(LayoutInflater.from(context))
            ViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       if(holder is AddDataViewHolder){
           holder.itemView.setOnClickListener {
               viewModel.addTag.postValue(true)
           }
       }else if(holder is ViewHolder){
           val tagRecord=viewModel.notUpTagRecord.value!![position]
            holder.binding.index.text= (position+1).toString()

           val starArr=tagRecord.startTime.split(" ")
           val entArr=tagRecord.endTime.split(" ")
           holder.binding.info.text="${tagRecord.destination}   ${starArr[0]}\n起止地点：${tagRecord.startType}-"
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