package com.zjdx.point.ui.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SPUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.databinding.ActivitySettingBinding
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.login.LoginActivity

class SettingActivity : BaseActivity() {

    lateinit var binding: ActivitySettingBinding
    override fun initRootView() {
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.titleBarSetting.leftIvTitleBar.setOnClickListener {
            finish()
        }
        binding.titleBarSetting.rightIvTitleBar.visibility=View.GONE
        binding.titleBarSetting.middleTvTitleBar.text="APP设置"
    }

    override fun initView() {
        binding.tvSettingAc.text = "当前APP版本：${AppUtils.getAppVersionName()}"
        binding.logoutSettingAc.setOnClickListener {
            SPUtils.getInstance().put(NameSpace.UID, "")
            SPUtils.getInstance().put(NameSpace.ISLOGIN, false)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}