package com.sourcecomb.ncrosby.mihoclient

import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import java.net.Socket
import java.nio.ByteBuffer
import java.util.concurrent.Future

private val hexArray = "0123456789ABCDEF".toCharArray()
fun ByteArray.toHex(): String {
    val hexChars = CharArray(this.size * 2)
    for (j in this.indices) {
        val v = this[j].toInt() and 0xFF
        hexChars[j * 2] = hexArray[v.ushr(4)]
        hexChars[j * 2 + 1] = hexArray[v and 0x0F]
    }
    return String(hexChars)
}

enum class RemoteAction(val value: Byte) {
    MOUSE_MOVE(3), MOUSE_CLICK(4)
}

class RemoteHost(hostname: String) {
    private lateinit var socket: Socket
    private var thread: Future<Unit>

    init {
        thread = doAsync {
            socket = Socket(hostname, 1234)
            socket.tcpNoDelay = true
        }
    }

    private fun send(msg: ByteBuffer): Future<Unit> {
        msg.position(0)
        val buf = ByteArray(msg.capacity())
        msg.get(buf)
        return send(buf)
    }

    private fun send(msg: ByteArray): Future<Unit> {
        thread = thread.doAsyncResult {
            Log.d("RemoteHost", "Writing message ${msg.toHex()}")
            val stream = socket.getOutputStream()
            stream.write(msg)
            stream.flush()
        }

        return thread
    }

    fun sendMouseMove(dx: Int, dy: Int): Future<Unit> {
        return send(ByteBuffer.allocate(5)
                .put(RemoteAction.MOUSE_MOVE.value)
                .putShort(dx.toShort())
                .putShort(dy.toShort()))
    }

    fun sendMouseButton(btn: Int, isDown: Boolean): Future<Unit> {
        return send(ByteBuffer.allocate(3)
                .put(RemoteAction.MOUSE_CLICK.value)
                .put((btn - 1).toByte())
                .put(if (isDown) 1.toByte() else 0.toByte()))
    }

    fun sendMouseClick(btn: Int, timer: Long = 100): Future<Unit> {
        sendMouseButton(btn, true)
        thread = thread.doAsyncResult { Thread.sleep(timer) }
        return sendMouseButton(btn, false)
    }
}
