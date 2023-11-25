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
    private var client = OkHttpClient()
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        Log.d("NLAll", "onNotificationPosted: $packageName")

        // BIDV: com.vnpay.bidv
        // Messenger: com.facebook.orca
        if(packageName != "com.vnpay.bidv" && packageName != "com.facebook.orca") {
            Log.d("NLAll", "Ignore onNotificationPosted")
            return
        }

        val title : String
        val content : String
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
            content = extras.getCharSequence("android.text").toString()
            Log.d("NLAll", "content: $content")
        } else {
            content = ""
        }

        val key = getKey("QLVR", content, 10)
        val amount = getAmount(content)

        if(key != "" && amount != "") {
            Log.d("NLAll", "send data to server:\nKey: $key\nAmount: $amount")
            sendDataToServer(title, key, amount)
        } else {
            Log.d("NLAll", "can not get key or amount")
        }

        i.putExtra("title", title)
        i.putExtra("key", key)
        i.putExtra("amount", amount)
        sendBroadcast(i)
    }

    private  fun getKey(head : String, content : String, totalLength : Int) : String {
        val index = content.indexOf(head)
        if(index != -1 && content.length - index > totalLength - 1) {
            return content.substring(index, index + totalLength)
        }
        return ""
    }

    private fun getAmount(content: String) : String {
        val headKey = "Số tiền: +"
        val tailKey = "VND."
        val startIndex = content.indexOf(headKey)
        if(startIndex != -1) {
            val endIndex = content.indexOf(tailKey, startIndex)
            if(endIndex != -1) {
                return content.substring(startIndex + headKey.length, endIndex)
            } else {
                Log.d("NLAll", "end key is not found")
            }
        }
        return ""
    }

    private fun sendDataToServer(title : String?, key : String?, amount : String) {
        val secretKey = "R1IC7I58XKKXPPAJXAGMGDJ3KWUI7U"
        val url = HttpUrl.Builder().scheme("https")
            .host(hostName)
            .addPathSegment("system")
            .addPathSegment("handleNotification")
            .addQueryParameter("secretKey", secretKey)
            .addQueryParameter("title", title)
            .addQueryParameter("key", key)
            .addQueryParameter("amount", amount)
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