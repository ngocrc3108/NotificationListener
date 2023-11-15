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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Objects


class MainActivity : Activity() {
    private var txtView: TextView? = null
    private var nReceiver: NotificationReceiver? = null
    //https://api.telegram.org/bot6170746237:AAGRau8vFZ_X3KMJHBMBygwJwZvJJ_3tDW0/sendMessage?chat_id=5318496948&text=Hello
    private val appUrl = "https://api.telegram.org/bot6170746237:AAGRau8vFZ_X3KMJHBMBygwJwZvJJ_3tDW0/sendMessage?chat_id=5318496948&text=Hello"
    var client = OkHttpClient()
    @SuppressLint("MissingInflatedId")
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
        override fun onReceive(context: Context, intent: Intent) {
            val textData = intent.getStringExtra("text")
            val titleData = intent.getStringExtra("title")
            txtView!!.text = "$titleData: $textData \n${txtView!!.text}"

            val request = Request.Builder()
                .url(appUrl)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    //txtView!!.text = "onFailure \n${txtView!!.text}"
                }

                override fun onResponse(call: Call, response: Response) {

                }
            })
        }
    }
}