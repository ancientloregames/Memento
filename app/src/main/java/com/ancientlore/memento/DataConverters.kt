package com.ancientlore.memento

import android.arch.persistence.room.TypeConverter
import java.text.DateFormat
import java.util.*

class DataConverters {

	@TypeConverter
	fun deserializeDate(str: String) = DateFormat.getInstance().parse(str)

	@TypeConverter
	fun serializeDate(date: Date) = date.toString()
}