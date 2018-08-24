package com.ancientlore.memento

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import android.support.annotation.IntDef
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.ancientlore.memento.extensions.createChannel
import com.ancientlore.memento.extensions.marshall
import com.ancientlore.memento.extensions.schedule
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.text.DateFormat
import java.util.*

@Entity(tableName = "alarms")
data class Alarm(@PrimaryKey(autoGenerate = true) var id: Long = 0,
				 @field:ColumnInfo var title: String = "",
				 @field:ColumnInfo var message: String = "",
				 @field:ColumnInfo var date: Date = Date(),
				 @field:ColumnInfo var snooze: Int = 0,
				 @field:ColumnInfo var sound: Uri = Settings.System.DEFAULT_ALARM_ALERT_URI,
				 @field:ColumnInfo var activeDays: BooleanArray,
				 @field:ColumnInfo var withVibration: Boolean,
				 @field:ColumnInfo var enabled: Boolean): Parcelable {

	constructor(parcel: Parcel) : this(
			parcel.readLong(),
			parcel.readString(),
			parcel.readString(),
			Date(parcel.readLong()),
			parcel.readInt(),
			parcel.readParcelable<Uri>(Uri::class.java.classLoader),
			parcel.createBooleanArray(),
			parcel.readInt() != 0,
			parcel.readInt() != 0)

	constructor(templateAlarm: Alarm) : this(
			templateAlarm.id,
			templateAlarm.title + "",
			templateAlarm.message + "",
			Date(templateAlarm.date.time),
			templateAlarm.snooze,
			templateAlarm.sound,
			templateAlarm.activeDays.copyOf(),
			templateAlarm.withVibration,
			templateAlarm.enabled
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeLong(id)
		parcel.writeString(title)
		parcel.writeString(message)
		parcel.writeLong(date.time)
		parcel.writeInt(snooze)
		parcel.writeParcelable(sound, flags)
		parcel.writeBooleanArray(activeDays)
		parcel.writeInt(if (withVibration) 1 else 0)
		parcel.writeInt(if (enabled) 1 else 0)
	}

	override fun describeContents() = 0

	override fun equals(other: Any?): Boolean {
		if (this !== other && other is Alarm) {
			if (id != other.id) return false
			if (title != other.title) return false
			if (message != other.message) return false
			if (date != other.date) return false
			if (snooze != other.snooze) return false
			if (sound != other.sound) return false
			if (!Arrays.equals(activeDays, other.activeDays)) return false
			if (withVibration != other.withVibration) return false
			if (enabled != other.enabled) return false
		}

		return true
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + title.hashCode()
		result = 31 * result + message.hashCode()
		result = 31 * result + date.hashCode()
		result = 31 * result + snooze.hashCode()
		result = 31 * result + sound.hashCode()
		result = 31 * result + Arrays.hashCode(activeDays)
		result = 31 * result + withVibration.hashCode()
		result = 31 * result + enabled.hashCode()
		return result
	}

	fun schedule(context: Context) {
		cancel(context)

		if (enabled) scheduleInternal(context)
	}

	private fun scheduleInternal(context: Context) {
		val intent = Intent(context, AlarmReceiver::class.java)
		// Can't pass Parcelable directly on API 24+ due to https://issuetracker.google.com/issues/37097877
		intent.putExtra(AlarmReceiver.EXTRA_ALARM_BYTES, marshall())
		val operation = PendingIntent.getBroadcast(context, id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

		(context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
				.schedule(AlarmManager.RTC_WAKEUP, date.time, operation)

		(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
				.notify(id.toInt(), createAlarmNotice(context))
	}

	fun sheduleNextAlarm(context: Context) {
		val calendar = Calendar.getInstance()
		val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
		Alarm.getDaysToIncrement(currentDay, activeDays).takeIf { it != 0 }
				?.let { increment ->
					val nextCalendar = Calendar.getInstance()
					nextCalendar.timeInMillis = date.time
					nextCalendar.add(Calendar.DAY_OF_MONTH, increment)
					val nextAlarm = Alarm(this)
					nextAlarm.date = nextCalendar.time
					Log.i("AlarmReceiver", "delay: " + (nextCalendar.time.time - date.time))

					schedule(context)
				}
	}

	fun cancel(context: Context) {
		val intent = Intent(context, AlarmReceiver::class.java)
		val pendingIntent = PendingIntent.getBroadcast(context, id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

		(context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
				.apply { cancel(pendingIntent) }
	}

	private fun createAlarmNotice(context: Context) = getNotificationBuilder(context)
			.setSmallIcon(R.drawable.ic_alarm)
			.setTicker(composeAlarmTitle(context))
			.setContentTitle(composeAlarmTitle(context))
			.setContentInfo(context.getString(R.string.app_name))
			.setContentText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(date))
			.setPriority(NotificationManager.IMPORTANCE_HIGH)
			.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
			.addAction(R.drawable.ic_cancel, context.getString(R.string.cancel), createCancelPendingIntent(context))
			.addAction(R.drawable.ic_skip_arrow, context.getString(R.string.skip), createSkipPendingIntent(context))
			.setOngoing(true)
			.build()

	private fun getNotificationBuilder(context: Context): NotificationCompat.Builder {
		return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			NotificationCompat.Builder(context, getAlarmChannel(context).id)
		else NotificationCompat.Builder(context, AlarmReceiver.alarmChannelId)
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun getAlarmChannel(context: Context): NotificationChannel {
		if (alarmChannel == null) {
			val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			alarmChannel = notificationManager.getNotificationChannel(AlarmReceiver.alarmChannelId)
					?: notificationManager.createChannel(AlarmReceiver.alarmChannelId, context.getString(R.string.alarms), NotificationManager.IMPORTANCE_HIGH)
		}
		return alarmChannel!!
	}

	private fun composeAlarmTitle(context: Context) = if (title.isNotEmpty()) title else context.getString(R.string.alarm) + ' ' + id

	private fun createCancelPendingIntent(context: Context): PendingIntent {
		val intent = Intent(context, ActionReceiver::class.java)
		intent.action = ActionReceiver.ACTION_CANCEL_ALARM
		intent.putExtra(ActionReceiver.EXTRA_ALARM, Alarm@this)
		return PendingIntent.getBroadcast(context, id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
	}

	private fun createSkipPendingIntent(context: Context): PendingIntent {
		val intent = Intent(context, ActionReceiver::class.java)
		intent.action = ActionReceiver.ACTION_SKIP_ALARM
		intent.putExtra(ActionReceiver.EXTRA_ALARM, Alarm@this)
		return PendingIntent.getBroadcast(context, id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
	}

	companion object CREATOR : Parcelable.Creator<Alarm> {

		var alarmChannel: NotificationChannel? = null

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