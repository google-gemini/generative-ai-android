package dev.shreyaspatil.ai.client.generativeai.platform

import org.slf4j.LoggerFactory

actual object Log {
    private val logger = LoggerFactory.getLogger("GenerativeAI")

    actual fun d(tag: String, message: String) {
        logger.debug("[$tag] $message")
    }

    actual fun w(tag: String, message: String) {
        logger.warn("[$tag] $message")
    }

    actual fun e(tag: String, message: String) {
        logger.error("[$tag] $message")
    }
}