package com.zimaspace.zimaos

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}

class Hello {
    fun world(): String {
        return  "Hello, World!"
    }
}