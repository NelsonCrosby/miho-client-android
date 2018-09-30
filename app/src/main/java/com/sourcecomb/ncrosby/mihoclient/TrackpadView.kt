package com.sourcecomb.ncrosby.mihoclient

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class TrackpadView : View {
    interface OnMouseMoveListener {
        fun onEvent(dx: Int, dy: Int)
    }

    interface OnMouseClickListener {
        fun onEvent()
    }

    var accelFactor: Float = 1f
    var tapClickDelay: Int = 100

    private var mouseMoveListener: OnMouseMoveListener? = null
    private var mouseClickListener: OnMouseClickListener? = null

    private var lastX: Float = 0f
    private var lastY: Float = 0f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    fun setOnMouseMoveListener(l: OnMouseMoveListener) {
        mouseMoveListener = l
    }

    fun setOnMouseMoveListener(l: (view: TrackpadView, dx: Int, dy: Int) -> Unit) {
        mouseMoveListener = object : OnMouseMoveListener {
            override fun onEvent(dx: Int, dy: Int) {
                l(this@TrackpadView, dx, dy)
            }
        }
    }

    fun setOnMouseClickListener(l: OnMouseClickListener) {
        mouseClickListener = l
    }

    fun setOnMouseClickListener(l: (view: TrackpadView) -> Unit) {
        mouseClickListener = object : OnMouseClickListener {
            override fun onEvent() {
                l(this@TrackpadView)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)

        if (event == null) { return false }

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("TrackpadView", "Registered touch start")
                lastX = event.x
                lastY = event.y
                true
            }
            MotionEvent.ACTION_UP -> {
                Log.d("TrackpadView", "Registered touch end")
                if (event.eventTime - event.downTime < tapClickDelay) {
                    Log.d("TrackpadView", "Touch was brief; sending click")
                    if (mouseClickListener != null)
                        mouseClickListener!!.onEvent()
                }
                true
            }
            MotionEvent.ACTION_MOVE -> {
                val newX = event.x
                val newY = event.y
                val x = ((newX - lastX) * accelFactor).toInt()
                val y = ((newY - lastY) * accelFactor).toInt()

                if (x != 0 && y != 0) {
                    lastX = newX
                    lastY = newY
                    Log.d("TrackpadView", "Moving mouse ($x, $y)")
                    if (mouseMoveListener != null) {
                        mouseMoveListener!!.onEvent(x, y)
                    }
                }

                true
            }
            else -> false
        }
    }
}
