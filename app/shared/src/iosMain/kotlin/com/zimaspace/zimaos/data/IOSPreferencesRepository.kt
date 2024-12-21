package com.zimaspace.zimaos.data

import com.zimaspace.zimaos.domain.repository.PreferencesRepository
import platform.Foundation.NSUserDefaults

class OSPreferencesRepository : PreferencesRepository {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun saveBaseUrl(url: String) {
        userDefaults.setObject(url, KEY_BASE_URL)
    }

    override fun getBaseUrl(): String =
        userDefaults.stringForKey(KEY_BASE_URL)

    override suspend fun pingAddress(address: String): Boolean {
        // TODO: Implement iOS specific ping mechanism
        return true
    }

    companion object {
        private const val KEY_BASE_URL = "base_url"
    }
}
