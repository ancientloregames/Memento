package com.ancientlore.memento

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import android.support.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

@Entity(tableName = "alarms")
data class Alarm(@PrimaryKey(autoGenerate = true) var id: Long = 0,
				 @field:ColumnInfo var title: String = "",
				 @field:ColumnInfo var message: String = "",
				 @field:ColumnInfo var date: Date = Date(),
				 @field:ColumnInfo var sound: Uri = Settings.System.DEFAULT_ALARM_ALERT_URI,
				 @field:ColumnInfo var activeDays: BooleanArray,
				 @field:ColumnInfo var enabled: Boolean): Parcelable {

	constructor(parcel: Parcel) : this(
			parcel.readLong(),
			parcel.readString(),
			parcel.readString(),
			Date(parcel.readLong()),
			parcel.readParcelable<Uri>(Uri::class.java.classLoader),
			parcel.createBooleanArray(),
			parcel.readInt() != 0)

	constructor(templateAlarm: Alarm) : this(
			templateAlarm.id,
			templateAlarm.title + "",
			templateAlarm.message + "",
			Date(templateAlarm.date.time),
			templateAlarm.sound,
			templateAlarm.activeDays.copyOf(),
			templateAlarm.enabled
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeLong(id)
		parcel.writeString(title)
		parcel.writeString(message)
		parcel.writeLong(date.time)
		parcel.writeParcelable(sound, flags)
		parcel.writeBooleanArray(activeDays)
		parcel.writeInt(if (enabled) 1 else 0)
	}


	override fun describeContents() = 0

	companion object CREATOR : Parcelable.Creator<Alarm> {

		override fun createFromParcel(parcel: Parcel) = Alarm(parcel)

		override fun newArray(size: Int) = arrayOfNulls<Alarm>(size)

		@Retention(RetentionPolicy.SOURCE)
		@IntDef(NONE, MON, TUE, WED, THU, FRI, SAT, SUN)
		internal annotation class Days
		const val NONE = -1
		const val MON = 0
		const val TUE = 1
		const val WED = 2
		const val THU = 3
		const val FRI = 4
		const val SAT = 5
		const val SUN = 6

		// TODO this looks bad. Need refactor
		fun getDaysToIncrement(currentDay: Int, activeDays: BooleanArray) =
				if (activeDays[dayToPos(currentDay)]) 0
				else if (activeDays.any { it }) {
					val currentPos = dayToPos(currentDay)
					val nextPos = if (currentPos < SUN) currentPos + 1 else MON
					var incremet = 1
					for (i in nextPos until activeDays.size) {
						if (!activeDays[i]) incremet++
						else break
					}
					incremet
				}
				else 0

		fun getNextAlarmDay(currentDay: Int, activeDays: BooleanArray) : Int {
			val currentPos = dayToPos(currentDay)
			val nextPos = if (currentPos < SUN) currentPos + 1 else MON
			for (i in nextPos until activeDays.size) {
				if (activeDays[i])
					return posToDay(i)
			}
			return NONE
		}

		private fun dayToPos(day: Int) = when (day) {
			Calendar.MONDAY -> MON
			Calendar.TUESDAY -> TUE
			Calendar.WEDNESDAY -> WED
			Calendar.THURSDAY -> THU
			Calendar.FRIDAY -> FRI
			Calendar.SATURDAY -> SAT
			Calendar.SUNDAY -> SUN
			else -> NONE
		}

		private fun posToDay(day: Int) = when (day) {
			MON -> Calendar.MONDAY
			TUE -> Calendar.TUESDAY
			WED -> Calendar.WEDNESDAY
			THU -> Calendar.THURSDAY
			FRI -> Calendar.FRIDAY
			SAT -> Calendar.SATURDAY
			SUN -> Calendar.SUNDAY
			else -> NONE
		}
	}
}