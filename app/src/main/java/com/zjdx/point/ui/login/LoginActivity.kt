package com.zjdx.point.ui.login

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.annotation.StringRes
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils

import com.zjdx.point.NameSpace
import com.zjdx.point.R
import com.zjdx.point.data.bean.SysUser
import com.zjdx.point.databinding.ActivityLoginBinding
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.main.MainActivity
import com.zjdx.point.ui.register.RegisterActivity


class LoginActivity : BaseActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding


    override fun initRootView() {

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        binding.titleBarLoginAc.leftIvTitleBar.visibility = View.GONE
        binding.titleBarLoginAc.rightIvTitleBar.visibility = View.GONE
        binding.titleBarLoginAc.middleTvTitleBar.text = "登录注册"
        binding.registerLoginAc.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun initViewMoedl() {

        val username = binding.username
        val password = binding.password
        val login = binding.loginLoginAc
        val loading = binding.loading

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.sysUser != null) {
                updateUiWithUser(loginResult.sysUser)
            }

            if (loginResult.code == 0) {

                SPUtils.getInstance().put(NameSpace.ISLOGIN, true)
                SPUtils.getInstance().put(NameSpace.UID, it.sysUser.usercode)

                //Complete and destroy login activity once successful
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                ToastUtils.showLong(it.msg)
            }

        })

        loginViewModel.errorModel.observe(this, {
            dismissProgressDialog()

        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }


            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }

    }


    private fun updateUiWithUser(model: SysUser) {
        val welcome = getString(R.string.welcome)
        val displayName = model.usercode
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }


}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}