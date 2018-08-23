package com.ancientlore.memento

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ancientlore.memento.extensions.unmarshall

class AlarmReceiver: BroadcastReceiver() {

	override fun onReceive(context: Context, intent: Intent) {
		intent.getByteArrayExtra(EXTRA_ALARM_BYTES)?.run {
			val alarm = unmarshall(Alarm.CREATOR)

			alarm.sheduleNextAlarm(context)

			showNoticeActivity(context, alarm)
		}
	}

	private fun showNoticeActivity(context: Context, alarm: Alarm) {
		Intent(context, NoticeActivity::class.java).run {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK
			putExtra(EXTRA_ALARM, alarm)
			context.startActivity(this)
		}
	}

	companion object {
		const val EXTRA_ALARM = "alarm"
		const val EXTRA_ALARM_BYTES = "alarm_message_bytes"

		const val alarmChannelId = "alarms"
	}
}