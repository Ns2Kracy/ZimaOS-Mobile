package com.zimaspace.zimaos.domain.model

data class ConnectionConfig(
    val address: String,
    val isZerotier: Boolean
) {
    companion object {
        fun isValidZerotierID(input: String): Boolean =
            input.matches(Regex("^[0-9a-fA-F]{16}\$"))

        fun isValidIPAddress(input: String): Boolean =
            input.matches(Regex("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$"))
    }
}
