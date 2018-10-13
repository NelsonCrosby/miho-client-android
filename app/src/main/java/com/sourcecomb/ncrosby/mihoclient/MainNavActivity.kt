package com.sourcecomb.ncrosby.mihoclient

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main_nav.*

class MainNavActivity : AppCompatActivity(), RemoteConnectionManager {
    private lateinit var remoteHost: RemoteHost
    private var hostname: String? = null
    private var port: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_nav)
        setSupportActionBar(toolbar)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        remoteHost = ViewModelProviders.of(this).get(RemoteHost::class.java)
        remoteHost.onClose {
            disconnect()
        }
    }

    override val connected: Boolean
        get() = remoteHost.connected

    override fun connect(hostname: String, port: Int, onDone: () -> Unit) {
        this.hostname = hostname
        this.port = port

        val localDone = { successful: Boolean ->
            if (successful)
                onDone()
            else
                disconnect()
        }

        Log.d("MainNavActivity", "Connecting to $hostname:$port")
        if (port == 0) {
            remoteHost.connect(hostname, onDone = localDone)
        } else {
            remoteHost.connect(hostname, port, localDone)
        }
    }
    override fun disconnect() {
        if (connected) {
            Log.d("MainNavActivity", "Disconnecting from $hostname:$port")
            remoteHost.close()
        }

        findNavController(R.id.nav_host).navigate(R.id.connectionInfoFragment)
    }

    override val mouse: RemoteConnectionManager.MouseSubsystemManager =
            object : RemoteConnectionManager.MouseSubsystemManager {
                override fun move(dx: Int, dy: Int) {
                    remoteHost.mouseSubsystem.move(dx, dy)
                }

                override fun button(btn: Int, isDown: Boolean) {
                    remoteHost.mouseSubsystem.button(btn, isDown)
                }
            }

    override fun onStart() {
        super.onStart()
        val hostname = hostname
        if (hostname != null) {
            connect(hostname, port) {}
        }
    }

    override fun onStop() {
        super.onStop()
        disconnect()
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host).navigateUp()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settingsFragment -> {
                findNavController(R.id.nav_host).navigate(R.id.action_settings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
