package com.sourcecomb.ncrosby.mihoclient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController

class MainNavActivity : AppCompatActivity() {

    private lateinit var remoteHost: RemoteHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_nav)

        remoteHost = ViewModelProviders.of(this).get(RemoteHost::class.java)
    }

    override fun onStart() {
        super.onStart()
        if (remoteHost.hostname != null) {
            remoteHost.connect {}
        }
    }

    override fun onStop() {
        super.onStop()
        if (remoteHost.connected) {
            remoteHost.close()
        }
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host).navigateUp()
}
