package com.ancientlore.memento

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.concurrent.Executors

class ActionReceiver: BroadcastReceiver() {
	companion object {
		const val ACTION_CANCEL_ALARM = "com.ancientlore.memento.action.CANCEL_ALARM"
		const val ACTION_SKIP_ALARM = "com.ancientlore.memento.action.SKIP_ALARM"

		const val EXTRA_ALARM_ID = "alarm_message_id"
		const val EXTRA_ALARM = "alarm_message"
	}

	override fun onReceive(context: Context, intent: Intent) {
		when (intent.action) {
			ACTION_CANCEL_ALARM ->
				intent.getLongExtra(EXTRA_ALARM_ID, -1).takeIf { it != -1L }?.let { cancelAlarm(context, it) }
			ACTION_SKIP_ALARM ->
				intent.getParcelableExtra<Alarm>(EXTRA_ALARM)?.let { skipAlarm(context, it) }
		}
	}

	private fun cancelAlarm(context: Context, alarmId: Long) {
		AlarmReceiver.cancelAlarm(context, alarmId.toInt())
		deleteAlarmFormDb(context, alarmId)
		deleteNotice(context, alarmId.toInt())
	}

	private fun skipAlarm(context: Context, alarm: Alarm) {
		AlarmReceiver.cancelAlarm(context, alarm.id.toInt())
		AlarmReceiver.sheduleNextAlarm(context, alarm)
	}

	private fun deleteNotice(context: Context, alarmId: Int) {
		val noticeManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		noticeManager.cancel(alarmId)
	}

	private fun deleteAlarmFormDb(context: Context, alarmId: Long) {
		Executors.newSingleThreadExecutor().submit {
			AlarmsDatabase.getInstance(context).alarmDao().deleteById(alarmId)
		}
	}
}