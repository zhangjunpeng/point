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
        taggingViewModel.selectLoaction.observe(this) {
            PopWindowUtil.instance.showTimePicker(requireContext()) { date, view ->
                startTime = date
                binding.startTime.text = DateUtil.dateFormat.format(date)
            }
        }
        binding.endTime.setOnClickListener {
            PopWindowUtil.instance.showTimePicker(requireContext()) { date, view ->
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
                ToastUtils.showLong("请选择在速度轴上选择开始点位")
                return@setOnClickListener
            }
            if (endTime == null) {
                ToastUtils.showLong("请选择点击选择结束时间")
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
            val tagRecord = TagRecord(
                startTime = DateUtil.dateFormat.format(startTime),
                endTime = DateUtil.dateFormat.format(endTime),
                startType = binding.startType.text.toString(),
                endType = binding.endType.text.toString(),
                travelodel = taggingViewModel.tarvelModelList.joinToString(separator = ","),
                destination = binding.dis.text.toString(),
                desc = binding.desc.text.toString(),
                )

            taggingViewModel.insertTagRecord(tagRecord = tagRecord)
            this@TagInfoFragment.dismiss()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = TagInfoFragment()
    }
}