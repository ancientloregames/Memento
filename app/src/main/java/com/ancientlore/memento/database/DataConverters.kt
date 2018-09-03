package com.ancientlore.memento.database

import android.arch.persistence.room.TypeConverter
import android.net.Uri
import java.util.*

class DataConverters {

	@TypeConverter
	fun deserializeDate(time: Long) = Date(time)

	@TypeConverter
	fun serializeDate(date: Date) = date.time

	@TypeConverter
	fun deserializeBooleanArray(string: String) : BooleanArray {
		val result = BooleanArray(string.length)
		for (i in 0 until string.length) {
			result[i] = string[i] == '1'
		}
		return result
	}

	@TypeConverter
	fun serializeBooleanArray(array: BooleanArray) : String {
		val result = StringBuilder(array.size)
		for (item in array) {
			result.append(if (item) "1" else "0")
		}
		return result.toString()
	}

	@TypeConverter
	fun deserializeUri(path: String) = (Uri.parse(path) ?: Uri.EMPTY)!!

	@TypeConverter
	fun serializeUri(uri: Uri) = uri.path!!
}