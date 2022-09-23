package com.zjdx.point.ui.edit

import android.R
import android.content.Intent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityRegisterBinding
import com.zjdx.point.event.ChooseEvent
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.main.MainActivity
import com.zjdx.point.ui.viewmodel.ViewModelFactory
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class EditUserInfoActivity : BaseActivity() {
    lateinit var binding: ActivityRegisterBinding

    var id: Int? = null

    var isFouce = false

    val editUserInfoViewModel by viewModels<EditUserInfoViewModel> {
        ViewModelFactory((application as PointApplication).travelRepository)
    }

    override fun initRootView() {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isFouce = intent.getBooleanExtra("isEidit", false)
        if (isFouce) {
            ToastUtils.showLong("请完善信息！")
        }

        EventBus.getDefault().register(this)

    }


    override fun initView() {
        binding.titleBarRegisterAc.leftIvTitleBar.setOnClickListener {
            finish()
        }
        binding.titleBarRegisterAc.rightIvTitleBar.visibility = View.GONE
        binding.passwordLinearRegisterAc.visibility = View.GONE
        binding.rePasswordLinearRegisterAc.visibility=View.GONE
        binding.reTelphoneLayRegisterAc.visibility=View.GONE

        binding.titleBarRegisterAc.middleTvTitleBar.text = "修改信息"
        binding.registerBtRegisterAc.text = "修改信息"

        binding.addressRegisterAc.setOnClickListener {
            startActivity(Intent(this@EditUserInfoActivity, ChooseAddressActivity::class.java))
        }

        binding.ageRegisterAc.adapter =
            ArrayAdapter(this, R.layout.simple_spinner_item, editUserInfoViewModel.ageList).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

        binding.minsalaryRegisterAc.adapter = ArrayAdapter(
            this, R.layout.simple_spinner_item, editUserInfoViewModel.salaryList
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.haveBicRegisterAc.adapter = ArrayAdapter(
            this, R.layout.simple_spinner_item, editUserInfoViewModel.hasBicList
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.haveCarRegisterAc.adapter = ArrayAdapter(
            this, R.layout.simple_spinner_item, editUserInfoViewModel.hasCarList
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.haveVeRegisterAc.adapter = ArrayAdapter(
            this, R.layout.simple_spinner_item, editUserInfoViewModel.hasVeList
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }



        binding.registerBtRegisterAc.setOnClickListener {
            if (id == null) {
                ToastUtils.showLong("网络异常")
                return@setOnClickListener
            }

            if (binding.telphoneRegisterAc.editableText.toString()!=binding.reTelphoneRegisterAc.editableText.toString()){
                Toast.makeText(this, "两次输入的电话不一致，请检查", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
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
            val age = editUserInfoViewModel.ageList[binding.ageRegisterAc.selectedItemPosition]
            val address = binding.addressRegisterAc.text.toString()
            val salary =
                editUserInfoViewModel.salaryList[binding.minsalaryRegisterAc.selectedItemPosition]

            showProgressDialog()
            editUserInfoViewModel.editUserInfo(
                id.toString(),
                userCode,
                userName,
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
        editUserInfoViewModel.sysUserLiveData.observe(this) { user ->
            dismissProgressDialog()
            id = user.id
            user!!.address?.let {
                binding.addressRegisterAc.setText(it)
            }
            user.age.let {
                var posi = editUserInfoViewModel.ageList.indexOf(it.toString())
                if (posi == -1) {
                    posi = 0
                }
                binding.ageRegisterAc.setSelection(posi)
            }
            user.salary?.let {
                var posi = editUserInfoViewModel.salaryList.indexOf(it)
                if (posi == -1) {
                    posi = 0
                }
                binding.minsalaryRegisterAc.setSelection(posi)
            }
            user.has_bicycle?.let {
                if (it==0) {
                    binding.haveBicRegisterAc.setSelection(0)
                } else {
                    binding.haveBicRegisterAc.setSelection(1)
                }
            }
            user.has_car?.let {
                if (it==0) {
                    binding.haveCarRegisterAc.setSelection(0)
                } else {
                    binding.haveCarRegisterAc.setSelection(1)
                }
            }
            user.has_vehicle?.let {
                if (it==0) {
                    binding.haveVeRegisterAc.setSelection(0)
                } else {
                    binding.haveVeRegisterAc.setSelection(1)
                }
            }
//            user.minsalary?.let {
//                binding.minsalaryRegisterAc.setText(it)
//            }
            user.telphone?.let {
                binding.telphoneRegisterAc.setText(it)
                binding.reTelphoneRegisterAc.setText(it)
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

        }
        editUserInfoViewModel.errorBack.observe(this) {
            dismissProgressDialog()
            if (it.code == 0) {
                ToastUtils.showLong("修改成功")
                finish()
                if (isFouce) {
                    startActivity(Intent(this, MainActivity::class.java))
                }

            } else {
                ToastUtils.showLong(it.msg)
            }
        }
    }

    @Subscribe
    fun onReceiveEvent(event:ChooseEvent){
        binding.addressRegisterAc.text=event.address
    }

    override fun initData() {
        showProgressDialog()
        editUserInfoViewModel.getUserInfo()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()

    }

}