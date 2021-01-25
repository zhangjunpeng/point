package com.zjdx.point.ui.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.R
import com.zjdx.point.data.repository.EditRepository
import com.zjdx.point.databinding.ActivityRegisterBinding
import com.zjdx.point.ui.base.BaseActivity

class EditUserInfoActivity : BaseActivity() {
    lateinit var binding: ActivityRegisterBinding

    var id: Int? = null

    val editUserInfoViewModel by viewModels<EditUserInfoViewModel> {
        EditUserInfoViewModelFactory(EditRepository())
    }

    override fun initRootView() {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun initView() {
        binding.titleBarRegisterAc.leftIvTitleBar.setOnClickListener {
            finish()
        }
        binding.titleBarRegisterAc.rightIvTitleBar.visibility = View.GONE
        binding.passwordLinearRegisterAc.visibility = View.GONE

        binding.titleBarRegisterAc.middleTvTitleBar.text = "修改信息"
        binding.registerBtRegisterAc.text = "修改信息"

        binding.registerBtRegisterAc.setOnClickListener {
            if (id == null) {
                ToastUtils.showLong("网络异常")
                return@setOnClickListener
            }
            showProgressDialog()
            val userCode = SPUtils.getInstance().getString(NameSpace.UID)
            val userName = binding.usernameRegisterAc.editableText.toString()
            val password = binding.passwordRegisterAc.editableText.toString()
            val telphone = binding.telphoneRegisterAc.editableText.toString()
            val note = binding.remarkRegisterAc.editableText.toString()
            val sex = if (binding.manRegisterAc.isChecked) {
                0
            } else {
                1
            }
            val age = binding.ageRegisterAc.editableText.toString()
            val address = binding.addressRegisterAc.editableText.toString()
            val minsalary = binding.minsalaryRegisterAc.editableText.toString()
            val maxsalary = binding.maxsalaryRegisterAc.editableText.toString()
            editUserInfoViewModel.editUserInfo(
                id.toString(),
                userCode,
                userName,
                telphone,
                note,
                sex,
                age,
                address,
                minsalary,
                maxsalary
            )
        }
    }

    override fun initViewMoedl() {
        editUserInfoViewModel.sysUserLiveData.observe(this, { user ->
            dismissProgressDialog()
            id = user.id

            user!!.address?.let {
                binding.addressRegisterAc.setText(it)
            }
            user.age.let {
                binding.ageRegisterAc.setText(it.toString())
            }
            user.maxsalary?.let {
                binding.maxsalaryRegisterAc.setText(it)
            }
            user.minsalary?.let {
                binding.minsalaryRegisterAc.setText(it)
            }
            user.telphone?.let {
                binding.telphoneRegisterAc.setText(it)
            }
            user.username?.let {
                binding.usernameRegisterAc.setText(it)
            }
            user.usercode.let {
                binding.usercodeRegisterAc.setText(it)
                binding.usercodeRegisterAc.isEnabled = false
            }
            user.sex?.let {
                if (it == "0") {
                    binding.manRegisterAc.isChecked = true
                } else {
                    binding.womanregisterAc.isChecked = true
                }
            }

        })
        editUserInfoViewModel.errorBack.observe(this, {
            dismissProgressDialog()
            if (it.code == 0) {
                ToastUtils.showLong("修改成功")
                finish()
            } else {
                ToastUtils.showLong(it.msg)
            }
        })
    }

    override fun initData() {
        showProgressDialog()
        editUserInfoViewModel.getUserInfo()
    }

}