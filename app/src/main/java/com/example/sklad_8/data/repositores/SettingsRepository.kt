package com.example.sklad_8.data.repositores

import com.example.sklad_8.data.prefs.SharedPrefsManager
import com.example.sklad_8.ui.settings.SettingsServerViewState
import kotlin.math.log

class SettingsRepository(private val prefs: SharedPrefsManager) {

    fun getDataServer(): SettingsServerViewState {
        val serverAddress = prefs.getServerAddress()
        val login = prefs.getLogin()
        val pass = prefs.getPass()
        return SettingsServerViewState(
            serverAddress = serverAddress,
            loginServer = login,
            passServer = pass
        )
    }

    fun saveServerAddress(address: String) {
        prefs.saveServerAddress(address)
    }

    fun saveServerLogin(login: String) {
        prefs.saveLogin(login)
    }

    fun saveServerPass(pass: String) {
        prefs.savePass(pass)
    }

}