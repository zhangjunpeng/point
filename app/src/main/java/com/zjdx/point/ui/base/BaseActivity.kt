package com.zjdx.point.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zjdx.point.R
import com.zjdx.point.data.bean.SubmitBackModel
import com.zjdx.point.databinding.DialogAbnormalBaseWorkAcBinding

open class BaseActivity : AppCompatActivity() {


    lateinit var saveListener: View.OnClickListener
    lateinit var cancelListener: View.OnClickListener

    lateinit var abnormalDialog: Dialog

    val typeList = arrayListOf<String>("骑行", "步行", "开车", "其他")



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
        if (this::progressDialog.isInitialized && progressDialog.isShowing) {
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


    fun showAbnormalDialog(msg: String, type: Int) {
        abnormalDialog = createAbnormalDialog(msg,type)

        if (!abnormalDialog.isShowing) {
            abnormalDialog.show()
        }
    }

    fun dismissAbnormalDialog() {
        if (this::abnormalDialog.isInitialized && abnormalDialog.isShowing) {
            abnormalDialog.dismiss()
        }
    }

    lateinit var dialogBinding:DialogAbnormalBaseWorkAcBinding

    open fun createAbnormalDialog(msg: String, type: Int): Dialog {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogBinding=DialogAbnormalBaseWorkAcBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)

        dialogBinding.titleDialogAbnormalBaseWorkAc.text = msg


        if (type==1){
            dialogBinding.cancelDialogAbnormalBaseWorkAc.setOnClickListener { dismissAbnormalDialog() }
            dialogBinding.submitDialogAbnormalBaseWorkAc.setOnClickListener(saveListener)
        }else{
            dialogBinding.cancelDialogAbnormalBaseWorkAc.text="停止记录"
            dialogBinding.submitDialogAbnormalBaseWorkAc.text="继续记录"
            dialogBinding.submitDialogAbnormalBaseWorkAc.setOnClickListener{
                dialog.dismiss()
            }
            dialogBinding.cancelDialogAbnormalBaseWorkAc.setOnClickListener(cancelListener)
        }

        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
}