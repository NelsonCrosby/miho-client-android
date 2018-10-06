package com.sourcecomb.ncrosby.mihoclient

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

class SimpleTrackpadFragment : Fragment() {

    private lateinit var remoteHost: RemoteHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        remoteHost = activity!!.run { ViewModelProviders.of(this).get(RemoteHost::class.java) }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_simple_trackpad, container, false)

        view.findViewById<TrackpadView>(R.id.trackpad).apply {
            accelFactor = 0.7f
            moveBufferTime = 8
            setOnMouseMoveListener { _, dx, dy ->
                remoteHost.mouseSubsystem.move(dx, dy)
            }
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

        view.findViewById<Button>(R.id.mouse_left).setOnTouchListener(btnOnTouch(1))
        view.findViewById<Button>(R.id.mouse_right).setOnTouchListener(btnOnTouch(2))
        view.findViewById<Button>(R.id.mouse_middle).setOnTouchListener(btnOnTouch(3))

        return view
    }
}
