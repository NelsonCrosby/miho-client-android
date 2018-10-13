package com.sourcecomb.ncrosby.mihoclient

interface RemoteConnectionManager {
    interface MouseSubsystemManager {
        fun move(dx: Int, dy: Int)
        fun button(btn: Int, isDown: Boolean)
    }

    val connected: Boolean

    fun connect(hostname: String, port: Int = 0, onDone: () -> Unit)
    fun disconnect()

    val mouse: MouseSubsystemManager
}
