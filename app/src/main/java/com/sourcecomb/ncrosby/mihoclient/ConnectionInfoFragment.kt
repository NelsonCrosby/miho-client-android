package com.sourcecomb.ncrosby.mihoclient

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import org.jetbrains.anko.bundleOf

class ConnectionInfoFragment : Fragment() {
    private lateinit var manager: RemoteConnectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = activity!! as RemoteConnectionManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_connection_info, container, false)
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
            view.findNavController().navigate(R.id.action_connect, bundleOf(
                    "hostname" to hostnameField.toString(),
                    "port" to portField.toString().toInt()
            ))
        }

        return view
    }
}
