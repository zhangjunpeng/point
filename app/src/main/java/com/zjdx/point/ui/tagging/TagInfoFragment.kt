package com.zjdx.point.ui.tagging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zjdx.point.databinding.FragmentTagInfoBinding
import com.zjdx.point.db.model.TagRecord
import com.zjdx.point.utils.DateUtil
import com.zjdx.point.utils.PopWindowUtil
import org.greenrobot.eventbus.EventBus
import java.util.*

class TagInfoFragment : BottomSheetDialogFragment() {

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
        binding.recyler.layoutManager = LinearLayoutManager(context)
        binding.recyler.adapter = InfoAdapter(requireContext(), taggingViewModel)
        binding.startTime.setOnClickListener {
            PopWindowUtil.instance.showTimePicker(
                requireContext(), isDialog = false
            ) { date, view ->
                startTime = date
                binding.startTime.text = DateUtil.dateFormat.format(date)
            }
        }
        binding.endTime.setOnClickListener {
            PopWindowUtil.instance.showTimePicker(
                requireContext(), isDialog = false
            ) { date, view ->
                endTime = date
                binding.endTime.text = DateUtil.dateFormat.format(date)
            }
        }
        binding.cancel.setOnClickListener {
            PopWindowUtil.instance.showDelDialog(requireContext()) {
                this@TagInfoFragment.dismiss()
            }
        }
        binding.confirm.setOnClickListener {
            if (binding.dis.text.isEmpty()) {
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
            if (binding.startType.text.isEmpty()) {
                ToastUtils.showLong("请输入起点类型")
                return@setOnClickListener

            }
            if (binding.endType.text.isEmpty()) {
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
                    it. endTime = DateUtil.dateFormat.format(endTime)
                    it. startType = binding.startType.text.toString()
                    it. endType = binding.endType.text.toString()
                    it. travelmodel = taggingViewModel.tarvelModelList.joinToString(separator = ",")
                    it. destination = binding.dis.text.toString()
                    it. desc = binding.desc.text.toString()
                }
                taggingViewModel.repository.updateTags(arrayListOf(taggingViewModel.addingTag!!))

                taggingViewModel.addingTag=null
            }else{
                val tagRecord = TagRecord(
                    startTime = DateUtil.dateFormat.format(startTime),
                    endTime = DateUtil.dateFormat.format(endTime),
                    startType = binding.startType.text.toString(),
                    endType = binding.endType.text.toString(),
                    travelmodel = taggingViewModel.tarvelModelList.joinToString(separator = ","),
                    destination = binding.dis.text.toString(),
                    desc = binding.desc.text.toString(),
                )
                val list = taggingViewModel.notUpTagRecord.value!!
                list.add(tagRecord)
                taggingViewModel.notUpTagRecord.value = list
            }


            this@TagInfoFragment.dismiss()
        }

        taggingViewModel.addingTag?.let {
            binding.startTime.text = it.startTime
            binding.endTime.text = it.endTime
            binding.startType.setText(it.startType)
            binding.endType.setText(it.endType)
            binding.dis.setText(it.destination)
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



}