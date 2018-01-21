package com.mataku.scrobscrob.app.presenter

import com.mataku.scrobscrob.app.model.api.Retrofit2LastFmClient
import com.mataku.scrobscrob.app.model.api.service.AuthMobileSessionService
import com.mataku.scrobscrob.app.ui.view.LoginViewCallback
import com.mataku.scrobscrob.app.util.AppUtil

class LoginPresenter(var accessable: Boolean, var view: LoginViewCallback) {
    private val appUtil = AppUtil()
    private val method = "auth.getMobileSession"

    fun authenticate(userName: String, password: String) {
        var params: MutableMap<String, String> = mutableMapOf()
        params["username"] = userName
        params["password"] = password
        params["method"] = method

        val apiSig: String = appUtil.generateApiSig(params)
        val client = Retrofit2LastFmClient.create(AuthMobileSessionService::class.java)
        val call = client.authenticate(userName, password, apiSig)
        try {
            val response = call.execute()
            val mobileSession = response?.body()?.mobileSession
            view.setSessionInfo(mobileSession!!.key, mobileSession.name)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun backToSettingsWhenLoggedIn(success: Boolean?, sessionKey: String) {
        if (success!! && sessionKey.isNotEmpty()) {
            if (accessable) {
                view.showSuccessMessage()
            } else {
                view.showSuccessMessage()
                view.showMessageToAllowAccessToNotification()
            }
            view.backToSettingsActivity()
        } else {
            view.focusOnPasswordView()
        }
    }
}