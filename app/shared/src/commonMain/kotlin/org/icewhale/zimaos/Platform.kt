package org.icewhale.zimaos

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform