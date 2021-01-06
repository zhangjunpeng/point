package com.zjdx.point.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zjdx.point.R
import com.zjdx.point.data.bean.SubmitBackModel

open class BaseActivity : AppCompatActivity() {


    var clickListener: View.OnClickListener = View.OnClickListener { }
    lateinit var linear: LinearLayout
    lateinit var editText: EditText
    private var spinner: Spinner? = null

    lateinit var abnormalDialog: Dialog

    val typeList = arrayListOf<String>("骑行", "步行", "开车", "其他")

    var qixingType = typeList[0]

    open var itemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            if (position == 3) {
                linear.visibility = View.VISIBLE

            } else {
                linear.visibility = View.GONE
                qixingType = typeList[position]
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRootView()
        initView()
        initViewMoedl()
        initData()
    }


    open fun initRootView() {

    }

    open fun initViewMoedl() {

    }

    open fun initView() {

    }

    open fun initData() {
    }

    lateinit var progressDialog: Dialog
    fun showProgressDialog() {
        if (!this::progressDialog.isInitialized) {
            progressDialog = createDialog()
        }

        if (!progressDialog.isShowing) {
            progressDialog.show()
        }
    }

    fun dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun createDialog(): Dialog {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_progress_base_recyler_sreen_ac)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    fun showAlerDialog(submitBackModel: SubmitBackModel) {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage(submitBackModel.msg)
            .setPositiveButton("确定") { dialog, which ->
                dialog.dismiss()
                if (submitBackModel.code == 200) {
                    finish()
                }
            }
            .create()
        alertDialog.show()
    }


    fun showAbnormalDialog(msg: String = "出行方式") {
        if (!this::abnormalDialog.isInitialized) {
            abnormalDialog = createAbnormalDialog(msg)
        }

        if (!abnormalDialog.isShowing) {
            abnormalDialog.show()
        }
    }

    fun dismissAbnormalDialog() {
        if (this::abnormalDialog.isInitialized && abnormalDialog.isShowing) {
            abnormalDialog.dismiss()
        }
    }

    open fun createAbnormalDialog(msg: String): Dialog {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_abnormal_base_work_ac)
        spinner = dialog.findViewById<Spinner>(R.id.spinner_dialog_abnormal_base_work_ac)
        linear = dialog.findViewById<LinearLayout>(R.id.Linear_other_dialog_abnormal_base_work_ac)
        editText = dialog.findViewById(R.id.other_dialog_abnormal_base_work_ac)

//        val adapter=ArrayAdapter(this,android.R.layout.simple_spinner_item)
        val adapter = ArrayAdapter<String>(
            this,
            R.layout.item_spinner_base_work,
            R.id.tv_item_spinner_base_work,
            typeList
        )
        spinner!!.adapter = adapter
        spinner!!.onItemSelectedListener = itemSelectedListener
        dialog.findViewById<TextView>(R.id.cancel_dialog_abnormal_base_work_ac)
            .setOnClickListener { dismissAbnormalDialog() }
        dialog.findViewById<TextView>(R.id.submit_dialog_abnormal_base_work_ac)
            .setOnClickListener(clickListener)
        val titleText = dialog.findViewById<TextView>(R.id.title_dialog_abnormal_base_work_ac)
        titleText.text = msg

        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
}