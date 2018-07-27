package com.ancientlore.memento

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat

class AlarmReceiver: BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {

		val alarm = intent.getParcelableExtra<Alarm>(AlarmActivity.EXTRA_ALARM)

		val noticeManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		val noticeIntent = Intent(context, NoticeActivity::class.java)
		noticeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

		val noticeId = alarm.id.toInt()
		val pendingIntent = PendingIntent.getActivity(context, noticeId, noticeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

		val noticeBuilder = NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_check)
				.setContentTitle(context.getString(R.string.app_name))
				.setContentText(alarm.title)
				.setTicker(alarm.title)
				.setContentIntent(pendingIntent)
				.setPriority(NotificationManager.IMPORTANCE_HIGH)

		noticeManager.notify(noticeId, noticeBuilder.build())
	}
}