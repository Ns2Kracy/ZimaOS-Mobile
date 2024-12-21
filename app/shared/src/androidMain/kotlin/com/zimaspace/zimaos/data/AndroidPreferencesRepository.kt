package com.zimaspace.zimaos.data

import android.content.Context
import com.zimaspace.zimaos.domain.repository.PreferencesRepository
import java.net.InetAddress

class AndroidPreferencesRepository(
    private val context: Context
) : PreferencesRepository {
    private val sharedPreferences = context.getSharedPreferences("ZimaOS", Context.MODE_PRIVATE)

    override fun saveBaseUrl(url: String) {
        sharedPreferences.edit().putString(KEY_BASE_URL, url).apply()
    }

    override fun getBaseUrl(): String? =
        sharedPreferences.getString(KEY_BASE_URL, null)

    override suspend fun pingAddress(address: String): Boolean = try {
        val inet = InetAddress.getByName(address)
        inet.isReachable(5000)
    } catch (e: Exception) {
        false
    }

    companion object {
        private const val KEY_BASE_URL = "base_url"
    }
}
