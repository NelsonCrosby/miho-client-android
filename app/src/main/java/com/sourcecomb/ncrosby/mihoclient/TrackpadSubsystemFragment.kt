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
import androidx.preference.PreferenceManager

class TrackpadSubsystemFragment : Fragment() {
    private lateinit var mouseManager: RemoteConnectionManager.MouseSubsystemManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mouseManager = (activity!! as RemoteConnectionManager).mouse
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_subsystem_trackpad, container, false)

        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        view.findViewById<TrackpadView>(R.id.trackpad).apply {
            setOnMouseMoveListener { _, dx, dy ->
                mouseManager.move(dx, dy)
            }
            setOnMouseClickListener {
                mouseManager.button(1, true)
                mouseManager.button(1, false)
            }

            accelFactor = prefs.getInt("pref_key_trackpad_sensitivity", 100) / 100f
            moveBufferTime = prefs.getInt("pref_key_trackpad_buffer", 8)
            prefs.registerOnSharedPreferenceChangeListener { prefs, key ->
                when (key) {
                    "pref_key_trackpad_sensitivity" -> {
                        accelFactor = prefs.getInt(key, 100) / 100f
                    }
                    "pref_key_trackpad_buffer" -> {
                        moveBufferTime = prefs.getInt(key, 8)
                    }
                }
            }
        }

        val btnOnTouch = { buttonID: Int ->
            { view: View, motionEvent: MotionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.d("SimpleTrackpad", "Pressing mouse $buttonID")
                        mouseManager.button(buttonID, true)
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                        Log.d("SimpleTrackpad", "Releasing mouse $buttonID")
                        mouseManager.button(buttonID, false)
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
