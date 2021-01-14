package com.zjdx.point.utils

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.*
import android.widget.*
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.zjdx.point.R
import com.zjdx.point.databinding.PopwindowUtilBinding
import com.zjdx.point.db.model.BaseListSreen
import com.zjdx.point.ui.base.BaseListViewModel


class PopWindowUtil : View.OnClickListener {

    companion object {
        val instance: PopWindowUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PopWindowUtil()
        }
    }

    private var baseSreenInfo: BaseListSreen? = null

    var codinglist = arrayListOf<String>(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "i",
        "G",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z"
    )

    var mContext: Context? = null


    fun createMergePopWindow(
        context: Context,
        baseListViewModel: BaseListViewModel
    ): PopupWindow {

        mContext = context
        baseSreenInfo = baseListViewModel.qualityListSreenLiveData.value?.copy() ?: BaseListSreen()

        val bindingPopwindowMergeBinding =
            PopwindowUtilBinding.inflate(LayoutInflater.from(mContext))

        val popupWindow = PopupWindow(
            bindingPopwindowMergeBinding.root, ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        bindingPopwindowMergeBinding.createtimePopwindow.isEnabled = true
        bindingPopwindowMergeBinding.endtimePopwindow.isEnabled = true
        bindingPopwindowMergeBinding.createtimePopwindow.setOnClickListener(this)
        bindingPopwindowMergeBinding.endtimePopwindow.setOnClickListener(this)


        bindingPopwindowMergeBinding.closeRecylerPopwindow.setOnClickListener {
            popupWindow.dismiss()
        }

        bindingPopwindowMergeBinding.resetSreenPopwindow.setOnClickListener {

            bindingPopwindowMergeBinding.createtimePopwindow.text = ""
            bindingPopwindowMergeBinding.endtimePopwindow.text = ""

        }

        bindingPopwindowMergeBinding.okSreenPopwindow.setOnClickListener {

            var startTime: String? = null
            var endTime: String? = null


            if (!TextUtils.isEmpty(bindingPopwindowMergeBinding.createtimePopwindow.text) && !TextUtils.isEmpty(
                    bindingPopwindowMergeBinding.endtimePopwindow.text
                )
            ) {
                startTime = bindingPopwindowMergeBinding.createtimePopwindow.text.toString()
                endTime = bindingPopwindowMergeBinding.endtimePopwindow.text.toString()
                if (startTime.replace("-", "").replace(" ", "").replace(":", "")
                        .toLong() >= endTime.replace("-", "").replace(" ", "").replace(":", "")
                        .toLong()
                ) {
                    ToastUtils.showShort("开始时间不得晚于或等于结束时间")
                    return@setOnClickListener
                }
            }




            baseListViewModel.qualityListSreenLiveData.value = baseSreenInfo

            popupWindow.dismiss()

        }



        return popupWindow
    }


    private fun getTimePicker(
        context: Context,
        onTimeSelectListener: OnTimeSelectListener
    ): TimePickerView {
        return TimePickerBuilder(context, onTimeSelectListener)
            .setType(booleanArrayOf(true, true, true, false, false, false)) //分别对应年月日时分秒，默认全部显示
            .setCancelText("取消") //取消按钮文字
            .setSubmitText("确定") //确认按钮文字
            //.setContentSize(18) //滚轮文字大小

            .setContentTextSize(18)
            .setTitleSize(20) //标题文字大小
            .setTitleText("日期选择") //标题文字
            .setOutSideCancelable(false) //点击屏幕，点在控件外部范围时，是否取消显示
            .isCyclic(true) //是否循环滚动
            .setTitleColor(Color.BLACK) //标题文字颜色
            .setTitleBgColor(mContext!!.resources!!.getColor(R.color.light_blue_600))
            .setBgColor(Color.WHITE)
            .setTextColorCenter(mContext!!.resources!!.getColor(R.color.light_blue_600))
            .setTextColorOut(Color.BLACK)
            .setSubmitColor(Color.WHITE) //确定按钮文字颜色
            .setCancelColor(Color.WHITE) //取消按钮文字颜色
//            .setBgColor(-0xcccccd) //滚轮背景颜色 Night mode //默认是1900-2100年
//            .setDate(selectedDate) // 如果不设置的话，默认是系统时间*/
//            .setRangDate(startDate, endDate) //起始终止年月日设定
            .setLabel("年", "月", "日", "时", "分", "秒")
            .isDialog(true) //是否显示为对话框样式
            .build()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.createtime_popwindow -> {
                val pvTime = getTimePicker(mContext!!) { date, view ->
                    if (v is TextView) {
                        v.text = TimeUtils.date2String(date, "yyyy-MM-dd")
                        baseSreenInfo!!.start_time = date.time

                    }
                }
                pvTime.show()
            }
            R.id.endtime_popwindow -> {
                val pvTime = getTimePicker(mContext!!) { date, view ->
                    if (v is TextView) {
                        v.text = TimeUtils.date2String(date, "yyyy-MM-dd")
                        baseSreenInfo!!.end_time = date.time
                    }
                }
                pvTime.show()
            }
        }
    }


}

