package com.zjdx.point.ui.base

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.zjdx.point.R
import com.zjdx.point.databinding.ActivityBaseBinding

open class BaseActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRootView()
        initViewMoedl()
        initView()

    }

    open fun initRootView() {

    }

    open fun initViewMoedl() {

    }
    open fun initView() {

    }

}