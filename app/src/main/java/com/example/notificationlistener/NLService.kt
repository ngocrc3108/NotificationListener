package com.example.notificationlistener

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NLService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        var titleData : String
        var textData : String
        val extras = sbn.notification.extras
        val i = Intent("CatchNotification")

        if(sbn.packageName != "com.facebook.orca")
            return

        if (extras.getString("android.title") != null) {
            titleData = extras.getString("android.title").toString()
        } else {
            titleData = ""
        }
        if (extras.getCharSequence("android.text") != null) {
            textData = extras.getCharSequence("android.text").toString()
        } else {
            textData = ""
        }

        i.putExtra("title", titleData)
        i.putExtra("text", textData)

        sendBroadcast(i)
    }
}