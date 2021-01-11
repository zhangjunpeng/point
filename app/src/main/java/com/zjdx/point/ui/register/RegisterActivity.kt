package com.zjdx.point.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.zjdx.point.NameSpace
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityRegisterBinding
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.main.MainActivity
import com.zjdx.point.ui.travel.TravelViewModel
import com.zjdx.point.ui.travel.TravelViewModelFactory
import com.zjdx.point.utils.SPUtils

class RegisterActivity : BaseActivity() {
    lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels<RegisterViewModel> {
        RegisterViewModelFactory((application as PointApplication).registerRepository)
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
        binding.titleBarRegisterAc.middleTvTitleBar.text = "注册"
        binding.registerBtRegisterAc.setOnClickListener {
            showProgressDialog()
            val userCode = binding.usercodeRegisterAc.editableText.toString()
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
            if (userCode.isNullOrEmpty() || password.isNullOrEmpty()) {
                Toast.makeText(this, "请输入用户名或密码", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            registerViewModel.registerUser(
                userCode,
                userName,
                password,
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
        super.initViewMoedl()
        registerViewModel.registerModel.observe(this, {
            dismissProgressDialog()
            if (it.code == 0) {

                SPUtils.getInstance(this).put(NameSpace.ISLOGIN, true)
                SPUtils.getInstance(this)
                    .put(NameSpace.UID, binding.usercodeRegisterAc.editableText.toString())
                Toast.makeText(this, "注册成功", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, it.msg, Toast.LENGTH_LONG).show()
            }
        })
    }
}