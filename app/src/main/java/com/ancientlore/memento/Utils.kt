package com.ancientlore.memento

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

object Utils {
	fun getRingtoneTitle(context: Context, ringtone: Uri) = RingtoneManager.getRingtone(context, ringtone)?.getTitle(context) ?: ""
}