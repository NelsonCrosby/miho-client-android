package com.sourcecomb.ncrosby.mihoclient

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
class SimpleTrackpadActivity : AppCompatActivity() {

    lateinit var remoteHost: RemoteHost

    private var accelFactor: Float = 0.6f

    private var lastX: Float = 0f
    private var lastY: Float = 0f

    @SuppressLint("ClickableViewAccessibility")     // Related to button clicks, which aren't all that clever yet.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_trackpad)

        remoteHost = RemoteHost(resources.getString(R.string.default_host))

        findViewById<View>(R.id.trackpad).setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("SimpleTrackpad", "Registered touch start")
                    lastX = motionEvent.x
                    lastY = motionEvent.y
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val newX = motionEvent.x
                    val newY = motionEvent.y
                    val x = ((newX - lastX) * accelFactor).toInt()
                    val y = ((newY - lastY) * accelFactor).toInt()

                    if (x != 0 && y != 0) {
                        lastX = newX
                        lastY = newY
                        Log.d("SimpleTrackpad", "Moving mouse ($x, $y)")
                        remoteHost.sendMouseMove(x, y)
                    }

                    true
                }
                else -> false
            }
        }

        val btnOnTouch = { buttonID: Int ->
            { view: View, motionEvent: MotionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.d("SimpleTrackpad", "Pressing mouse $buttonID")
                        remoteHost.sendMouseButton(buttonID, true)
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                        Log.d("SimpleTrackpad", "Releasing mouse $buttonID")
                        remoteHost.sendMouseButton(buttonID, false)
                        true
                    }
                    else -> false
                }
            }
        }

        findViewById<Button>(R.id.mouse_left).setOnTouchListener(btnOnTouch(1))
        findViewById<Button>(R.id.mouse_right).setOnTouchListener(btnOnTouch(2))
        findViewById<Button>(R.id.mouse_middle).setOnTouchListener(btnOnTouch(3))
    }
}

