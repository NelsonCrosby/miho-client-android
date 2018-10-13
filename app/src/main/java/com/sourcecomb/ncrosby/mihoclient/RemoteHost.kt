package com.sourcecomb.ncrosby.mihoclient

import android.util.Log
import androidx.lifecycle.ViewModel
import co.nstant.`in`.cbor.CborBuilder
import co.nstant.`in`.cbor.CborDecoder
import co.nstant.`in`.cbor.CborEncoder
import co.nstant.`in`.cbor.CborException
import co.nstant.`in`.cbor.model.Array
import co.nstant.`in`.cbor.model.DataItem
import co.nstant.`in`.cbor.model.Number
import co.nstant.`in`.cbor.model.UnicodeString
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.BufferedOutputStream
import java.net.Socket
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class RemoteHost: ViewModel() {
    enum class CoreAction(val value: Long) {
        QUERY_SUBSYSTEMS(0),
    }

    var connected: Boolean = false
        private set

    private lateinit var socket: Socket
    private lateinit var bufferedOutput: BufferedOutputStream
    private lateinit var cborEnc: CborEncoder
    private lateinit var cborDec: CborDecoder

    var mouseSubsystem: StubMouseSubsystem = StubMouseSubsystem(this@RemoteHost, 0)

    private lateinit var sendThread: Thread
    private lateinit var recvThread: Thread
    private var sendQueue: BlockingQueue<List<DataItem>> = LinkedBlockingQueue()
    private var recvQueue: BlockingQueue<(status: Int, value: DataItem) -> Unit> = LinkedBlockingQueue()

    fun connect(hostname: String, port: Int = 6446, onDone: () -> Unit) {
        doAsync {
            socket = Socket(hostname, port)
            socket.tcpNoDelay = true

            connected = true

            bufferedOutput = BufferedOutputStream(socket.getOutputStream())
            cborEnc = CborEncoder(bufferedOutput)
            cborDec = CborDecoder(socket.getInputStream())

            cborEnc.encode(CborBuilder()
                    .addArray()
                        .add(0)
                        .add(CoreAction.QUERY_SUBSYSTEMS.value)
                        .end()
                    .build())
            bufferedOutput.flush()
            Log.d("RemoteHost", "sent subsystem query")

            sendThread = Thread({
                try {
                    Log.d("RemoteHost", "starting send loop")
                    while (true) {
                        val msg = sendQueue.take()
                        val msgArray = Array()
                        msg.forEach { msgArray.add(it) }
                        cborEnc.encode(msgArray)
                        bufferedOutput.flush()
                    }
                } catch (exc: InterruptedException) {
                    Log.d("RemoteHost", "stopping send loop")
                }
            }, "RemoteHostSend")
            sendThread.start()

            recvThread = Thread({
                try {
                    Log.d("RemoteHost", "starting recv loop")
                    cborDec.decode {
                        val msg = (it as Array).dataItems
                        val subsys = (msg[0] as Number).value.toLong()
                        val status = (msg[1] as Number).value.toInt()

                        if (subsys == 0L && status < 0) {
                            val code = (msg[2] as Number).value.toLong()
                            val message = (msg[3] as UnicodeString).string
                            Log.d("RemoteHost", "ERR: $code, $message")
                        } else {
                            val value = msg[2]
                            recvQueue.take()(status, value)
                        }
                    }
                } catch (exc: CborException) {
                    Log.d("RemoteHost", "stopping recv loop (${exc.message})")
                }
            }, "RemoteHostRecv")
            recvThread.start()

            recvQueue.put { _, subsystems ->
                var i = 0L
                for (sysName in (subsystems as Array).dataItems) {
                    i += 1      // First subsystem has ID 1
                    val name = (sysName as UnicodeString).string
                    Log.d("RemoteHost", "got subsystem $name")
                    when (name) {
                        ":mouse" -> {
                            mouseSubsystem = MouseSubsystem(this@RemoteHost, i)
                        }
                    }
                }

                uiThread { onDone() }
            }
        }
    }

    private fun send(msg: List<DataItem>): Boolean = sendQueue.offer(msg)
    private fun send(msg: CborBuilder): Boolean = send(msg.build())

    private fun query(msg: List<DataItem>, callback: (status: Int, value: DataItem) -> Unit): Boolean {
        return if (recvQueue.offer(callback)) {
            sendQueue.put(msg)
            true
        } else { false }
    }

    fun close() {
        sendThread.interrupt()
        socket.close()
        connected = false
    }

    open class Subsystem(val host: RemoteHost, val id: Long)

    open class StubMouseSubsystem(host: RemoteHost, id: Long) : Subsystem(host, id) {
        open fun move(dx: Int, dy: Int) {}
        open fun button(btn: Int, isDown: Boolean) {}
        open fun click(btn: Int, timer: Long = 100) {}
    }

    class MouseSubsystem(host: RemoteHost, id: Long) : StubMouseSubsystem(host, id) {
        enum class MouseAction(val value: Long) {
            MOUSE_MOVE(3),
            MOUSE_BUTTON(4),
        }

        private fun CborBuilder.add(action: MouseAction) = add(action.value)

        override fun move(dx: Int, dy: Int) {
            host.send(CborBuilder()
                    .add(id)
                    .add(MouseAction.MOUSE_MOVE)
                    .add(dx.toLong())
                    .add(dy.toLong()))
        }

        override fun button(btn: Int, isDown: Boolean) {
            host.send(CborBuilder()
                    .add(id)
                    .add(MouseAction.MOUSE_BUTTON)
                    .add(btn.toLong() - 1)
                    .add(isDown))
        }

        override fun click(btn: Int, timer: Long) {
            button(btn, true)
            doAsync {
                Thread.sleep(timer)
                button(btn, false)
            }
        }
    }
}
