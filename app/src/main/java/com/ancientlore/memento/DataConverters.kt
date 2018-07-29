package com.ancientlore.memento

import android.arch.persistence.room.TypeConverter
import java.text.DateFormat
import java.util.*

class DataConverters {

	@TypeConverter
	fun deserializeDate(time: Long) = Date(time)

	@TypeConverter
	fun serializeDate(date: Date) = date.time
}