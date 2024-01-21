package dev.shreyaspatil.ai.client.generativeai.platform

actual object Log {
    actual fun d(tag: String, message: String) {
        println("[$tag] $message")
    }

    actual fun w(tag: String, message: String) {
        println("[$tag] $message")
    }

    actual fun e(tag: String, message: String) {
        println("[$tag] $message")
    }

}