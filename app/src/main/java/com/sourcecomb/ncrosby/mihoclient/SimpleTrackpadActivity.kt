package com.sourcecomb.ncrosby.mihoclient

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button

class SimpleTrackpadActivity : AppCompatActivity() {

    private lateinit var remoteHost: RemoteHost

    @SuppressLint("ClickableViewAccessibility")     // Related to button clicks, which aren't all that clever yet.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_trackpad)

        findViewById<TrackpadView>(R.id.trackpad).apply {
            accelFactor = 0.7f
            moveBufferTime = 8
            setOnMouseMoveListener { _, dx, dy -> remoteHost.mouseSubsystem.move(dx, dy) }
            setOnMouseClickListener {
                remoteHost.mouseSubsystem.click(1)
            }
        }

        val btnOnTouch = { buttonID: Int ->
            { view: View, motionEvent: MotionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.d("SimpleTrackpad", "Pressing mouse $buttonID")
                        remoteHost.mouseSubsystem.button(buttonID, true)
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                        Log.d("SimpleTrackpad", "Releasing mouse $buttonID")
                        remoteHost.mouseSubsystem.button(buttonID, false)
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

    override fun onStart() {
        super.onStart()
        remoteHost = RemoteHost(resources.getString(R.string.default_host))
    }

    override fun onStop() {
        super.onStop()
        remoteHost.close()
    }
}

