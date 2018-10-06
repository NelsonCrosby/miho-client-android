package com.sourcecomb.ncrosby.mihoclient

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main_nav.*

class MainNavActivity : AppCompatActivity() {

    private lateinit var remoteHost: RemoteHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_nav)
        setSupportActionBar(toolbar)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

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
