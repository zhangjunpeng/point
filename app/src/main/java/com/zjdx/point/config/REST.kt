package com.zjdx.point.config

class REST {
    companion object {
        val BaseUrl = "http://47.97.101.151:8889/personaltrack/"
        val upload = BaseUrl + "historicalTrack/insertHistoricalTrack"
        val appVersion = BaseUrl + "historicalTrack/selectMaxAppVersion"
        val login = BaseUrl + "login/loginSubmit"
        val register = BaseUrl + "sysUser/addUser"

        val userInfo = BaseUrl + "sysUser/getUserByCodeOrId"
        val editUserInfo = BaseUrl + "sysUser/modifyUser"

        val hisTravel = BaseUrl + "historicalTrack/selectTravels"
        val hisLocation = BaseUrl + "historicalTrack/selectHistoricalTrack"


    }
}