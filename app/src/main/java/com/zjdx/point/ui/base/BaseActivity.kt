package com.zjdx.point.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zjdx.point.R
import com.zjdx.point.bean.SubmitBackBean

open class BaseActivity : AppCompatActivity() {


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

    fun showAlerDialog(submitBackBean: SubmitBackBean) {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage(submitBackBean.msg)
            .setPositiveButton("确定") { dialog, which ->
                dialog.dismiss()
                if (submitBackBean.code == 200) {
                    finish()
                }
            }
            .create()
        alertDialog.show()
    }
}