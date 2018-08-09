package com.ancientlore.memento

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import java.util.*

class AlarmReceiver: BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {

		val alarm = intent.getParcelableExtra<Alarm>(AlarmActivity.EXTRA_ALARM)

		val noticeManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		val noticeIntent = Intent(context, NoticeActivity::class.java)
		noticeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

		val noticeId = alarm.id.toInt()
		val pendingIntent = PendingIntent.getActivity(context, noticeId, noticeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

		val noticeBuilder = NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_alarm)
				.setTicker(alarm.title)
				.setContentTitle(alarm.title)
				.setContentInfo(context.getString(R.string.app_name))
				.setContentText(alarm.message)
				.setContentIntent(pendingIntent)
				.setPriority(NotificationManager.IMPORTANCE_HIGH)
				.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
				.setVibrate(defaultVibratePattern)
				.setSound(alarm.sound)

		noticeManager.notify(noticeId, noticeBuilder.build())

		sheduleNextAlarm(context, alarm)
	}

	private fun sheduleNextAlarm(context: Context, alarm: Alarm) {
		val calendar = Calendar.getInstance()
		val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
		Alarm.getDaysToIncrement(currentDay, alarm.activeDays).takeIf { it != 0 }
				?.let { increment ->
					val nextCalendar = Calendar.getInstance()
					nextCalendar.timeInMillis = alarm.date.time
					nextCalendar.add(Calendar.DAY_OF_MONTH, increment)
					val nextAlarm = Alarm(alarm)
					nextAlarm.date = nextCalendar.time
					Log.i("AlarmReceiver", "delay: " + (nextCalendar.time.time - alarm.date.time))

					scheduleAlarm(context, nextAlarm)
				}
	}

	companion object {

		val defaultVibratePattern = longArrayOf(0, 250, 250, 250)

		fun resetAlarm(context: Context, alarm: Alarm) {
			AlarmReceiver.cancelAlarm(context, alarm.id.toInt())
			if (alarm.enabled) AlarmReceiver.scheduleAlarm(context, alarm)
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