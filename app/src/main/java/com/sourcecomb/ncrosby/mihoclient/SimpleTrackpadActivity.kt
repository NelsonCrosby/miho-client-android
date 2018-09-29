package com.sourcecomb.ncrosby.mihoclient

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class SimpleTrackpadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_trackpad)

        findViewById<View>(R.id.trackpad).apply {
            this.setOnDragListener { view, dragEvent -> false }
        }
    }
}
