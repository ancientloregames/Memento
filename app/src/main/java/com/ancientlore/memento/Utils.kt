package com.ancientlore.memento

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

object Utils {
	fun getRingtoneTitle(context: Context, ringtone: Uri) = RingtoneManager.getRingtone(context, ringtone)?.getTitle(context) ?: ""
}

fun Parcelable.marshall() : ByteArray {
	val parcel = Parcel.obtain()
	writeToParcel(parcel, 0)
	val bytes = parcel.marshall()
	parcel.recycle()
	return bytes
}

fun <T> ByteArray.unmarshall(creator: Parcelable.Creator<T>): T {
	val parcel = unmarshall()
	val result = creator.createFromParcel(parcel)
	parcel.recycle()
	return result
}

private fun ByteArray.unmarshall(): Parcel {
	val parcel = Parcel.obtain()
	parcel.unmarshall(this, 0, size)
	parcel.setDataPosition(0) // This is extremely important!
	return parcel
}