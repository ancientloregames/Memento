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
import java.text.DateFormat
import java.util.*

class AlarmReceiver: BroadcastReceiver() {

	override fun onReceive(context: Context, intent: Intent) {

		val alarm = intent.getByteArrayExtra(EXTRA_ALARM_BYTES).run { unmarshall(Alarm.CREATOR) }

		sheduleNextAlarm(context, alarm)

		Intent(context, NoticeActivity::class.java).run {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK
			putExtra(EXTRA_ALARM, alarm)
			context.startActivity(this)
		}
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
		const val EXTRA_ALARM = "alarm"
		const val EXTRA_ALARM_BYTES = "alarm_message_bytes"

		private const val alarmChannelName = "alarms"

		fun resetAlarm(context: Context, alarm: Alarm) {
			AlarmReceiver.cancelAlarm(context, alarm.id.toInt())
			if (alarm.enabled) AlarmReceiver.scheduleAlarm(context, alarm)
		}

		fun scheduleAlarm(context: Context, alarm: Alarm) {
			val intent = Intent(context, AlarmReceiver::class.java)
			// Can't pass Parcelable directly on API 24+ due to https://issuetracker.google.com/issues/37097877
			intent.putExtra(EXTRA_ALARM_BYTES, alarm.marshall())
			val pendingIntent =
					PendingIntent.getBroadcast(context, alarm.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

			val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
				alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.date.time, pendingIntent)
			else
				alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.date.time, pendingIntent)

			val noticeManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			noticeManager.notify(alarm.id.toInt(), createAlarmNotice(context, alarm))
		}

		private fun createAlarmNotice(context: Context, alarm: Alarm) =
			NotificationCompat.Builder(context, alarmChannelName)
					.setSmallIcon(R.drawable.ic_alarm)
					.setTicker(alarm.title)
					.setContentTitle(alarm.title)
					.setContentInfo(context.getString(R.string.app_name))
					.setContentText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(alarm.date))
					.setPriority(NotificationManager.IMPORTANCE_HIGH)
					.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
					.setOngoing(true)
					.addAction(R.drawable.ic_cancel, context.getString(R.string.cancel), createCancelPendingIntent(context, alarm.id.toInt()))
					.build()

		private fun createCancelPendingIntent(context: Context, alarmId: Int): PendingIntent {
			val intent = Intent(context, ActionReceiver::class.java).apply {
				action = ActionReceiver.ACTION_CANCEL_ALARM
				putExtra(ActionReceiver.EXTRA_ALARM_ID, alarmId)
			}
			return PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
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