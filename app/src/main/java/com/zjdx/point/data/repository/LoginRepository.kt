package com.zjdx.point.data.repository

import com.zjdx.point.data.DataSource
import com.zjdx.point.data.bean.Back
import com.zjdx.point.data.bean.LoginModel
import com.zjdx.point.data.bean.SysUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: DataSource) {

    // in-memory cache of the loggedInUser object
    var user: SysUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    suspend fun login(username: String, password: String): Back<LoginModel> {
        // handle login
        return withContext(Dispatchers.IO) {
            val result = dataSource.login(username, password)
            if (result is Back.Success) {
                setLoggedInUser(result.data.sysUser)
            }
            result
        }

    }

    private fun setLoggedInUser(loggedInUser: SysUser) {
        this.user = loggedInUser

    }
}