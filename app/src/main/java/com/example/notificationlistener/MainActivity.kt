package com.example.notificationlistener

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.TextView


class MainActivity : Activity() {
    private var txtView: TextView? = null
    private var nReceiver: NotificationReceiver? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        txtView = findViewById<View>(R.id.textView) as TextView
        nReceiver = NotificationReceiver()
        val filter = IntentFilter()
        filter.addAction("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE")
        registerReceiver(nReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(nReceiver)
    }

    internal inner class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val textData = intent.getStringExtra("text")
            val titleData = intent.getStringExtra("title")
            var temp = "$titleData: $textData \n${txtView!!.text}"
            txtView!!.text = temp
        }
    }
}