package com.ancientlore.memento

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ActionReceiver: BroadcastReceiver() {
	companion object {
		const val ACTION_CANCEL_ALARM = "com.ancientlore.memento.action.CANCEL_ALARM"

		const val EXTRA_ALARM_ID = "alarm_message_id"
	}

	override fun onReceive(context: Context, intent: Intent) {
		intent.getIntExtra(EXTRA_ALARM_ID, -1).takeIf { it != -1 }?.let {
			cancelAlarm(context, it)
			deleteNotice(context, it)
		}
	}

	private fun cancelAlarm(context: Context, alarmId: Int) {
		val intent = Intent(context, AlarmReceiver::class.java)
		val pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

		val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		alarmManager.cancel(pendingIntent)
	}

	private fun deleteNotice(context: Context, alarmId: Int) {
		val noticeManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		noticeManager.cancel(alarmId)
	}
}