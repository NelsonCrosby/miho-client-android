package com.sourcecomb.ncrosby.mihoclient

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController

class ConnectFragment : Fragment() {
    private lateinit var remoteHost: RemoteHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        remoteHost = activity!!.run { ViewModelProviders.of(this).get(RemoteHost::class.java) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_connect, container, false)
        val hostnameField = view.findViewById<EditText>(R.id.edit_hostname).text
        val portField = view.findViewById<EditText>(R.id.edit_port).text

        val sp = context?.getSharedPreferences("remember", Context.MODE_PRIVATE)
        if (sp != null) {
            val connectLast = sp.getString("connect_last", null)
            if (connectLast != null) {
                val connectParams = connectLast.split(':')
                val lastHostname = connectParams[0]
                val lastPort = connectParams[1]

                hostnameField.clear()
                hostnameField.insert(0, lastHostname)
                portField.clear()
                portField.insert(0, lastPort)
            }
        }

        view.findViewById<Button>(R.id.btn_connect).setOnClickListener { _ ->
            remoteHost.hostname = hostnameField.toString()
            remoteHost.port = portField.toString().toInt()

            val dialog = activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setMessage("Connecting to ${remoteHost.hostname}:${remoteHost.port}...")
                    setTitle("Connecting")
                }

                builder.create()
            }

            dialog?.show()
            remoteHost.connect {
                sp?.edit()
                        ?.putString("connect_last", "${remoteHost.hostname}:${remoteHost.port}")
                        ?.apply()

                dialog?.hide()
                view.findNavController().navigate(R.id.action_connected)
            }
        }

        return view
    }
}
