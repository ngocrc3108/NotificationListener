package com.example.notificationlistener

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class NLService : NotificationListenerService() {
    private val hostName = "htn-server.onrender.com"
    var client = OkHttpClient()
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        Log.d("NLAll", "onNotificationPosted: $packageName")

        // BIDV: com.vnpay.bidv
        // Messenger: com.facebook.orca
        if(packageName != "com.vnpay.bidv" && packageName != "com.facebook.orca") {
            Log.d("NLAll", "Ignore onNotificationPosted")
            return
        }

        var title : String
        var key : String
        val extras = sbn.notification.extras
        val i = Intent("CatchNotification")

        if (extras.getString("android.title") != null) {
            title = extras.getString("android.title").toString()
        } else {
            title = ""
        }

        if(title != "Thông báo BIDV") {
            Log.d("NLAll", "title is not match")
            return
        }

        if (extras.getCharSequence("android.text") != null) {
            key = extras.getCharSequence("android.text").toString()
        } else {
            key = ""
        }
        
        key = findKey("QLVR", key, 10)

        if(key != "") {
            Log.d("NLAll", "send key to server: $key")
            sendDataToServer(title, key)
        } else {
            Log.d("NLAll", "key is not found")
        }

        i.putExtra("title", title)
        i.putExtra("key", key)
        sendBroadcast(i)
    }

    private  fun findKey(key : String, text : String, totalLength : Int) : String {
        val index = text.indexOf(key)
        if(index != -1 && text.length - index > totalLength - 1) {
            return text.substring(index, index + totalLength)
        }
        return ""
    }

    private fun sendDataToServer(title : String?, key : String?) {
        val url = HttpUrl.Builder().scheme("https")
            .host(hostName)
            .addPathSegment("handleNotification")
            .addQueryParameter("title", title)
            .addQueryParameter("key", key)
            .build()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("NLAll", "onFailure")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("NLAll", "onResponse ${response.isSuccessful}")
            }
        })
    }
}