package com.zjdx.point.ui.register

import android.R
import android.content.Intent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.blankj.utilcode.util.SPUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityRegisterBinding
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.main.MainActivity

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

        binding.ageRegisterAc.adapter =
            ArrayAdapter(this, R.layout.simple_spinner_item, registerViewModel.ageList).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

        binding.minsalaryRegisterAc.adapter = ArrayAdapter(
            this, R.layout.simple_spinner_item, registerViewModel.salaryList
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.haveBicRegisterAc.adapter = ArrayAdapter(
            this, R.layout.simple_spinner_item, registerViewModel.hasBicList
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.haveCarRegisterAc.adapter = ArrayAdapter(
            this, R.layout.simple_spinner_item, registerViewModel.hasCarList
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.haveVeRegisterAc.adapter = ArrayAdapter(
            this, R.layout.simple_spinner_item, registerViewModel.hasVeList
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

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
            val age = registerViewModel.ageList[binding.ageRegisterAc.selectedItemPosition]
            val address = binding.addressRegisterAc.editableText.toString()
            val salary =
                registerViewModel.salaryList[binding.minsalaryRegisterAc.selectedItemPosition]
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
                salary,
                binding.haveBicRegisterAc.selectedItemPosition == 0,
                binding.haveCarRegisterAc.selectedItemPosition == 0,
                binding.haveVeRegisterAc.selectedItemPosition == 0,
            )
        }
    }

    override fun initViewMoedl() {
        super.initViewMoedl()
        registerViewModel.registerModel.observe(this, {
            dismissProgressDialog()
            if (it.code == 0) {

                SPUtils.getInstance().put(NameSpace.ISLOGIN, true)
                SPUtils.getInstance()
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