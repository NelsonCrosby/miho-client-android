package com.sourcecomb.ncrosby.mihoclient

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

        view.findViewById<Button>(R.id.btn_connect).setOnClickListener { _ ->
            val hostnameField = view.findViewById<EditText>(R.id.edit_hostname)
            val portField = view.findViewById<EditText>(R.id.edit_port)

            remoteHost.hostname = hostnameField.text.toString()
            remoteHost.port = portField.text.toString().toInt()

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
                dialog?.hide()
                view.findNavController().navigate(R.id.action_connected)
            }
        }

        return view
    }
}
