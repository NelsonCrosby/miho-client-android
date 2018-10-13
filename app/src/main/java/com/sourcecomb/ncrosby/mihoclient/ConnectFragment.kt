package com.sourcecomb.ncrosby.mihoclient

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController


private const val ARG_HOSTNAME = "hostname"
private const val ARG_PORT = "port"

class ConnectFragment : Fragment() {
    private var hostname: String = ""
    private var port: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            hostname = it.getString(ARG_HOSTNAME)!!
            port = it.getInt(ARG_PORT)
            Log.d("ConnectFragment", "Arguments: " +
                    "$ARG_HOSTNAME = $hostname; " +
                    "$ARG_PORT = $port")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        arguments?.let {
            hostname = it.getString(ARG_HOSTNAME)!!
            port = it.getInt(ARG_PORT)
            Log.d("ConnectFragment", "Arguments: " +
                    "$ARG_HOSTNAME = $hostname; " +
                    "$ARG_PORT = $port")
        }

        val view = inflater.inflate(R.layout.fragment_connect, container, false)
        view.findViewById<TextView>(R.id.lbl_connect_to).apply {
            text = String.format(text.toString(), hostname, port)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val manager = activity!! as RemoteConnectionManager

        if (manager.connected) {
            manager.disconnect()
        } else {
            manager.connect(hostname, port) {
                view!!.findNavController().navigate(R.id.action_connected)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(hostname: String, port: Int) =
                ConnectFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_HOSTNAME, hostname)
                        putInt(ARG_PORT, port)
                    }
                }
    }
}
