package com.zjdx.point.ui.tagging

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.ComponentDialog
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.zjdx.point.databinding.DialogInputModelBinding
import com.zjdx.point.databinding.ItemTagAddTrackBinding
import com.zjdx.point.utils.PopWindowUtil

class InfoAdapter(val context: Context, val viewModl: TaggingViewModel) :
    RecyclerView.Adapter<InfoAdapter.ViewHolder>() {

    val modelList = ArrayList<String>().apply {
        add("步行")
        add("自行车")
        add("电动自行车")
        add("公交车")
        add("小汽车")
        add("地铁")
        add("其它")
    }


    inner class ViewHolder(val binding: ItemTagAddTrackBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTagAddTrackBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, index: Int) {
        holder.binding.add.setOnClickListener {
            viewModl.tarvelModelList.add("步行")
            notifyDataSetChanged()
        }
        holder.binding.del.setOnClickListener {
            PopWindowUtil.instance.showDelDialog(context) {
                if (viewModl.tarvelModelList.size != 1) {
                    viewModl.tarvelModelList.removeAt(index)
                    notifyDataSetChanged()
                }

            }

        }
        holder.binding.up.setOnClickListener {
            if (index > 0) {
                val str = viewModl.tarvelModelList[index]
                viewModl.tarvelModelList.removeAt(index)
                viewModl.tarvelModelList.add(index - 1, str)
            }
            notifyDataSetChanged()
        }
        holder.binding.down.setOnClickListener {
            if (index < viewModl.tarvelModelList.size - 1) {
                val str = viewModl.tarvelModelList[index]
                viewModl.tarvelModelList.removeAt(index)
                viewModl.tarvelModelList.add(index + 1, str)
            }
            notifyDataSetChanged()
        }
        holder.binding.model.adapter =
            ArrayAdapter(context, android.R.layout.simple_spinner_item, modelList).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        holder.binding.model.setSelection(modelList.indexOf(viewModl.tarvelModelList[index]))

        holder.binding.model.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, posi: Int, id: Long
            ) {
                if (posi == modelList.size - 1) {
                    val dialog =
                        ComponentDialog(context, android.R.style.Theme_Material_Light_Dialog)
                    val dialogBinding =
                        DialogInputModelBinding.inflate(LayoutInflater.from(context), parent, false)
                    dialog.setContentView(dialogBinding.root)
                    dialogBinding.confirm.setOnClickListener {
                        if (dialogBinding.input.text.toString().isEmpty()) {
                            ToastUtils.showLong("请输入交通方式")
                            return@setOnClickListener
                        }
                        (view as TextView).text = dialogBinding.input.text
                        viewModl.tarvelModelList[index] = dialogBinding.input.text.toString()
                        dialog.dismiss()
                    }
                    dialogBinding.cancel.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                } else {
                    viewModl.tarvelModelList[index] = modelList[posi]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    override fun getItemCount(): Int {
        return viewModl.tarvelModelList.size
    }


}