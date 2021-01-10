package com.zjdx.point.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.zjdx.point.databinding.ActivityRegisterBinding
import com.zjdx.point.ui.base.BaseActivity

class RegisterActivity : BaseActivity() {
    lateinit var binding: ActivityRegisterBinding

    override fun initRootView() {
        binding=ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        binding.titleBarRegisterAc.leftIvTitleBar.setOnClickListener {
            finish()
        }
        binding.titleBarRegisterAc.rightIvTitleBar.visibility=View.GONE
        binding.titleBarRegisterAc.middleTvTitleBar.text="注册"
        binding.registerBtRegisterAc.setOnClickListener {

        }
    }
}