package com.zjdx.point.ui.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zjdx.point.databinding.ActivityAccountBinding
import com.zjdx.point.ui.base.BaseActivity

class AccountActivity : BaseActivity() {

    lateinit var binding: ActivityAccountBinding
    override fun initRootView() {
        binding=ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}