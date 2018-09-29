package com.sourcecomb.ncrosby.mihoclient

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button

class SimpleTrackpadActivity : AppCompatActivity() {

    lateinit var remoteHost: RemoteHost

    private var lastX: Int = 0
    private var lastY: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_trackpad)

        remoteHost = RemoteHost(resources.getString(R.string.default_host))

        findViewById<View>(R.id.trackpad).setOnTouchListener { _, motionEvent ->
            Log.d("SimpleTrackpad", "Trackpad got motionEvent $motionEvent")
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("SimpleTrackpad", "Registered touch start")
                    lastX = motionEvent.x.toInt()
                    lastY = motionEvent.y.toInt()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val newX = motionEvent.x.toInt()
                    val newY = motionEvent.y.toInt()
                    val x = (newX - lastX)
                    val y = (newY - lastY)
                    lastX = newX
                    lastY = newY
                    Log.d("SimpleTrackpad", "Moving mouse ($x, $y)")
                    remoteHost.sendMouseMove(x, y)
                    true
                }
                else -> false
            }
        }

        findViewById<Button>(R.id.mouse_left).setOnClickListener {
            Log.d("SimpleTrackpad", "Buttons got mouse_left clicked")
            remoteHost.sendMouseClick(1)
        }
        findViewById<Button>(R.id.mouse_right).setOnClickListener {
            Log.d("SimpleTrackpad", "Buttons got mouse_right clicked")
            remoteHost.sendMouseClick(2)
        }
        findViewById<Button>(R.id.mouse_middle).setOnClickListener {
            Log.d("SimpleTrackpad", "Buttons got mouse_middle clicked")
            remoteHost.sendMouseClick(3)
        }
    }
}
