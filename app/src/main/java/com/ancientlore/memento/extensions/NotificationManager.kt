package com.ancientlore.memento.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.support.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
fun NotificationManager.createChannel(id: String, title: String, importance: Int): NotificationChannel {
	val channel = NotificationChannel(id, title, importance)
	createNotificationChannel(channel)
	return channel
}