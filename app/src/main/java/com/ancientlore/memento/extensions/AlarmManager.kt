package com.ancientlore.memento.extensions

import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build

fun AlarmManager.schedule(type: Int, time: Long, operation: PendingIntent) {
	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
		set(type, time, operation)
	else
		setExact(type, time, operation)
}