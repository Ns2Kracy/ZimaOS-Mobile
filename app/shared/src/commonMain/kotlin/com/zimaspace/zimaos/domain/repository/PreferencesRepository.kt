package com.zimaspace.zimaos.domain.repository

interface PreferencesRepository {
    fun saveBaseUrl(url: String)
    fun getBaseUrl(): String?
    suspend fun pingAddress(address: String): Boolean
}
