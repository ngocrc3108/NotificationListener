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
    @SuppressLint("MissingInflatedId", "UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        txtView = findViewById<View>(R.id.textView) as TextView
        nReceiver = NotificationReceiver()
        val filter = IntentFilter()
        filter.addAction("CatchNotification")
        registerReceiver(nReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(nReceiver)
    }

    internal inner class NotificationReceiver : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {
            val key = intent.getStringExtra("key")
            val title = intent.getStringExtra("title")
            val amount = intent.getStringExtra("amount")
            txtView!!.text = "$title: $key $amount \n${txtView!!.text}"
        }
    }
}