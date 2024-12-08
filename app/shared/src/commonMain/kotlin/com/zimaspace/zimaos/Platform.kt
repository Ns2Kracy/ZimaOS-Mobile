package com.zimaspace.zimaos

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform