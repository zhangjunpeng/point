package com.zjdx.point.ui.tagging

import android.R
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zjdx.point.databinding.FragmentTagInfoBinding
import com.zjdx.point.db.model.TagRecord
import com.zjdx.point.utils.DateUtil
import com.zjdx.point.utils.PopWindowUtil
import java.util.*

class TagInfoFragment : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentTagInfoBinding

    private val taggingViewModel: TaggingViewModel by activityViewModels<TaggingViewModel>()

    var startTime: Date? = null
    var endTime: Date? = null
//    var startType: String? = null
//    var endType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTagInfoBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyler.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding.recyler.adapter = InfoAdapter(requireContext(), taggingViewModel)


        binding.disSp.adapter = ArrayAdapter(
            requireContext(), R.layout.simple_spinner_item, taggingViewModel.travelDis
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.startTypeSp.adapter = ArrayAdapter(
            requireContext(), R.layout.simple_spinner_item, taggingViewModel.types
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.endTypeSp.adapter = ArrayAdapter(
            requireContext(), R.layout.simple_spinner_item, taggingViewModel.types
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.disSp.onItemSelectedListener = this
        binding.startTypeSp.onItemSelectedListener = this
        binding.endTypeSp.onItemSelectedListener = this

        binding.startTime.setOnClickListener {
            val cel = Calendar.getInstance()
            if (startTime != null) {
                cel.time = startTime!!
            }
            PopWindowUtil.instance.showTimePicker(
                requireContext(), isDialog = false,
                selectedDate = cel,
                onTimeSelectListener = { date, view ->
                    startTime = date
                    binding.startTime.text = DateUtil.dateFormat.format(date)
                },
            )
        }
        binding.endTime.setOnClickListener {
            val cel = Calendar.getInstance()
            if (endTime != null) {
                cel.time = endTime!!
            }
            PopWindowUtil.instance.showTimePicker(
                requireContext(), isDialog = false,
                selectedDate = cel,
                onTimeSelectListener = { date, view ->
                    endTime = date
                    binding.endTime.text = DateUtil.dateFormat.format(date)
                },
            )
        }
        binding.cancel.setOnClickListener {
            AlertDialog.Builder(requireContext()).setMessage("“本次修改将不被保存，是否退出？")
                .setPositiveButton("确定") { dialog, which ->
                    taggingViewModel.clearSelect()
                    this@TagInfoFragment.dismiss()
                }.setNegativeButton("取消") { dialog, which ->
                    dialog.dismiss()
                }.create().show()

        }
        binding.confirm.setOnClickListener {
            if (binding.desc.isVisible && binding.desc.text.isEmpty()) {
                ToastUtils.showLong("请输入目的地")
                return@setOnClickListener
            }
            if (startTime == null) {
                ToastUtils.showLong("请点击选择结束时间")
                return@setOnClickListener
            }
            if (endTime == null) {
                ToastUtils.showLong("请点击选择结束时间")
                return@setOnClickListener
            }

            if(startTime!!.after(endTime!!)){
                ToastUtils.showLong("到达时刻应晚于出发时刻!")
                return@setOnClickListener
            }
            if (binding.startType.isVisible && binding.startType.text.isEmpty()) {
                ToastUtils.showLong("请输入起点类型")
                return@setOnClickListener

            }
            if (binding.endType.isVisible && binding.endType.text.isEmpty()) {
                ToastUtils.showLong("请输入终点类型")
                return@setOnClickListener

            }
            if (taggingViewModel.tarvelModelList.size == 0) {
                ToastUtils.showLong("请添加出行方式")
                return@setOnClickListener
            }


            if (taggingViewModel.addingTag!=null){
                taggingViewModel.addingTag?.let {
                    it.startTime = DateUtil.dateFormat.format(startTime)
                    it.endTime = DateUtil.dateFormat.format(endTime)

                    if (binding.startType.isVisible) {
                        it.startType = binding.startType.text.toString()
                    } else {
                        it.startType = binding.startTypeSp.selectedItem.toString()
                    }
                    if (binding.endType.isVisible) {
                        it.endType = binding.endType.text.toString()
                    } else {
                        it.endType = binding.endTypeSp.selectedItem.toString()
                    }
                    it.travelmodel = taggingViewModel.tarvelModelList.joinToString(separator = ",")
                    if (binding.desc.isVisible) {
                        it.destination = binding.desc.text.toString()
                    } else {
                        it.destination = binding.disSp.selectedItem.toString()
                    }

                    it.desc = binding.desc.text.toString()
                }
                taggingViewModel.repository.updateTags(arrayListOf(taggingViewModel.addingTag!!))

                val list = taggingViewModel.notUpTagRecord.value!!
                var index = -1
                list.forEach {
                    if (it.id != null && it.id == taggingViewModel.addingTag!!.id) {
                        index = list.indexOf(it)
                    }
                }
                if (index != -1) {
                    list.removeAt(index)
                    list.add(index, taggingViewModel.addingTag!!)
                    taggingViewModel.notUpTagRecord.value = list
                }
                taggingViewModel.addingTag = null

            } else {
                val startType = if (binding.startType.isVisible) {
                    binding.startType.text.toString()
                } else {
                    binding.startTypeSp.selectedItem.toString()
                }
                val endType = if (binding.endType.isVisible) {
                    binding.endType.text.toString()
                } else {
                    binding.endTypeSp.selectedItem.toString()
                }
                val destination = if (binding.desc.isVisible) {
                    binding.desc.text.toString()
                } else {
                    binding.disSp.selectedItem.toString()
                }
                val tagRecord = TagRecord(
                    startTime = DateUtil.dateFormat.format(startTime),
                    endTime = DateUtil.dateFormat.format(endTime),
                    startType = startType,
                    endType = endType,
                    travelmodel = taggingViewModel.tarvelModelList.joinToString(separator = ","),
                    destination = destination,
                )
                val list = taggingViewModel.notUpTagRecord.value!!
                list.add(tagRecord)
                taggingViewModel.notUpTagRecord.value = list
            }

            taggingViewModel.clearSelect()
            this@TagInfoFragment.dismiss()
        }

        taggingViewModel.selectLoaction.observe(this) { loca ->
            if (loca == null) {
                binding.setStart.isSelected = false
                binding.setEnd.isSelected = false
                binding.setStart.setTextColor(Color.WHITE)
                binding.setEnd.setTextColor(Color.WHITE)

                binding.setStart.setOnClickListener(null)
                binding.setEnd.setOnClickListener(null)

            } else {
                binding.setStart.isSelected = true
                binding.setEnd.isSelected = true
                binding.setStart.setTextColor(Color.parseColor("#595959"))
                binding.setEnd.setTextColor(Color.parseColor("#595959"))
                binding.setStart.setOnClickListener {
                    binding.startTime.text = loca.creatTime
                    startTime = DateUtil.dateFormat.parse(loca.creatTime)
                }
                binding.setEnd.setOnClickListener {
                    binding.endTime.text = loca.creatTime
                    endTime = DateUtil.dateFormat.parse(loca.creatTime)

                }
            }
        }

        taggingViewModel.addingTag?.let {

            if (taggingViewModel.travelDis.contains(it.destination)) {
                val index=taggingViewModel.travelDis.indexOf(it.destination)
                binding.disSp.setSelection(index)
                binding.desc.visibility = View.GONE
            } else {
                binding.disSp.setSelection(taggingViewModel.travelDis.lastIndex)
                binding.desc.visibility = View.VISIBLE
                binding.desc.setText(it.destination)
            }

            if (taggingViewModel.types.contains(it.startType)) {
                binding.startTypeSp.setSelection(taggingViewModel.types.indexOf(it.startType))
                binding.startType.visibility = View.GONE
            } else {
                binding.startTypeSp.setSelection(taggingViewModel.types.lastIndex)
                binding.startType.visibility = View.VISIBLE
                binding.startType.setText(it.startType)
            }

            if (taggingViewModel.types.contains(it.endType)) {
                binding.endTypeSp.setSelection(taggingViewModel.types.indexOf(it.endType))
                binding.endType.visibility = View.GONE
            } else {
                binding.endTypeSp.setSelection(taggingViewModel.types.lastIndex)
                binding.endType.visibility = View.VISIBLE
                binding.endType.setText(it.endType)
            }

            binding.startTime.text = it.startTime
            binding.endTime.text = it.endTime
            startTime = DateUtil.dateFormat.parse(it.startTime)
            endTime = DateUtil.dateFormat.parse(it.endTime)
            binding.desc.setText(it.desc)
            taggingViewModel.tarvelModelList.clear()
            taggingViewModel.tarvelModelList.addAll(it.travelmodel.split(","))
            (binding.recyler.adapter as InfoAdapter).notifyDataSetChanged()
        }
    }


    companion object {

        @JvmStatic
        fun newInstance() = TagInfoFragment()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            binding.disSp.id -> {
                if (position == taggingViewModel.travelDis.lastIndex) {
                    binding.desc.visibility = View.VISIBLE
                } else {
                    binding.desc.visibility = View.GONE
                }
            }
            binding.startTypeSp.id -> {
                if (position == taggingViewModel.types.lastIndex) {
                    binding.startType.visibility = View.VISIBLE
                } else {
                    binding.startType.visibility = View.GONE
                }
            }
            binding.endTypeSp.id -> {
                if (position == taggingViewModel.types.lastIndex) {
                    binding.endType.visibility = View.VISIBLE
                } else {
                    binding.endType.visibility = View.GONE
                }
            }
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


}