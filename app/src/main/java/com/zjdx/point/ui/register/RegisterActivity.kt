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
import com.zjdx.point.event.ChooseEvent
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.edit.ChooseAddressActivity
import com.zjdx.point.ui.main.MainActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class RegisterActivity : BaseActivity() {
    lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels<RegisterViewModel> {
        RegisterViewModelFactory((application as PointApplication).registerRepository)
    }

    override fun initRootView() {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        EventBus.getDefault().register(this)

    }

    override fun initView() {
        binding.titleBarRegisterAc.leftIvTitleBar.setOnClickListener {
            finish()
        }
        binding.titleBarRegisterAc.rightIvTitleBar.visibility = View.GONE
        binding.titleBarRegisterAc.middleTvTitleBar.text = "注册"

        binding.addressRegisterAc.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, ChooseAddressActivity::class.java))
        }

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
            val userCode = binding.usercodeRegisterAc.editableText.toString()
            val userName = binding.usernameRegisterAc.editableText.toString()
            val password = binding.passwordRegisterAc.editableText.toString()
            val telphone = binding.telphoneRegisterAc.editableText.toString()
            val sex = if (binding.manRegisterAc.isChecked) {
                0
            } else {
                1
            }
            val age = registerViewModel.ageList[binding.ageRegisterAc.selectedItemPosition]
            val address = binding.addressRegisterAc.text.toString()
            val salary =
                registerViewModel.salaryList[binding.minsalaryRegisterAc.selectedItemPosition]
            if (userCode.isNullOrEmpty() || password.isNullOrEmpty()) {
                Toast.makeText(this, "请输入用户名或密码", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (binding.telphoneRegisterAc.editableText.toString()!=binding.reTelphoneRegisterAc.editableText.toString()){
                Toast.makeText(this, "两次输入的电话不一致，请检查", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (binding.passwordRegisterAc.editableText.toString()!=binding.rePasswordRegisterAc.editableText.toString()){
                Toast.makeText(this, "两次输入的密码不一致，请检查", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            showProgressDialog()
            registerViewModel.registerUser(
                userCode,
                userName,
                password,
                telphone,
                "",
                sex,
                age,
                address,
                salary,
                binding.haveBicRegisterAc.selectedItemPosition == 1,
                binding.haveCarRegisterAc.selectedItemPosition == 1,
                binding.haveVeRegisterAc.selectedItemPosition == 1,

            )
        }
    }

    @Subscribe
    fun onReceiveEvent(event: ChooseEvent){
        binding.addressRegisterAc.text=event.address
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun initViewMoedl() {
        super.initViewMoedl()
        registerViewModel.registerModel.observe(this) {
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
        }
    }
}