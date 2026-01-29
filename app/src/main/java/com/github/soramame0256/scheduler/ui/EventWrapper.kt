package com.github.soramame0256.scheduler.ui

class EventWrapper<out T>(private val event: T) {
    var handled = false
        private set
    fun handle(block: (T) -> Unit) {
        if (!handled) {
            handled = true
            block(event)
        }
    }
}