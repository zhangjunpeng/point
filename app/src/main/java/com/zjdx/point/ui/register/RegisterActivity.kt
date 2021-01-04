package com.zjdx.point.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zjdx.point.databinding.ActivityRegisterBinding
import com.zjdx.point.ui.base.BaseActivity

class RegisterActivity : BaseActivity() {
    lateinit var binding: ActivityRegisterBinding

    override fun initRootView() {
        binding=ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}