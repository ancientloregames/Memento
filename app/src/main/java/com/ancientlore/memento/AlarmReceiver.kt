package com.ancientlore.memento

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
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

	companion object {

		fun resetAlarm(context: Context, alarm: Alarm) {
			AlarmReceiver.cancelAlarm(context, alarm.id.toInt())
			if (alarm.active) AlarmReceiver.scheduleAlarm(context, alarm)
		}

		fun scheduleAlarm(context: Context, alarm: Alarm) {
			val intent = Intent(context, AlarmReceiver::class.java)
			intent.putExtra(AlarmActivity.EXTRA_ALARM, alarm)
			val pendingIntent =
					PendingIntent.getBroadcast(context, alarm.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

			val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
				alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.date.time, pendingIntent)
			else
				alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.date.time, pendingIntent)
		}

		fun cancelAlarm(context: Context, alarmId: Int) {
			val intent = Intent(context, AlarmReceiver::class.java)
			val pendingIntent =
					PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

			val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
			alarmManager.cancel(pendingIntent)
		}
	}
}