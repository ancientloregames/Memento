package com.ancientlore.memento

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.net.Uri
import android.provider.Settings
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*

class AlarmActivityViewModel: ViewModel() {

	var id: Long = 0

	val titleField = ObservableField<String>("")
	val messageField = ObservableField<String>("")
	val periodField = ObservableField<String>("")
	val soundField = ObservableField<String>("")
	val vibroField = ObservableBoolean(true)

	/* FIXME two-way dataBinding doesn't work properly at this time. The binding of the minute attribute
	*  changes the hour filed in the View class https://issuetracker.google.com/issues/111948800
	*/
	val hoursField = ObservableInt(0)
	val minutesField = ObservableInt(0)

	private var period = BooleanArray(7) { false }
	private var soundUri = Settings.System.DEFAULT_ALARM_ALERT_URI

	private val choosePeriodEvent = PublishSubject.create<BooleanArray>()

	private val chooseSoundEvent = PublishSubject.create<Uri>()

	private val submitAlarmEvent = PublishSubject.create<Alarm>()

	private val deleteAlarmEvent = PublishSubject.create<Long>()

	fun setDate(date: Date?) {
		val calendar = Calendar.getInstance()
		date?.let { calendar.time = it }
		hoursField.set(calendar.get(Calendar.HOUR_OF_DAY))
		minutesField.set(calendar.get(Calendar.MINUTE))
	}

	fun setPeriod(period: BooleanArray, title: String) {
		this.period = period
		periodField.set(title)
	}

	fun setSound(uri: Uri, title: String) {
		soundUri = uri
		soundField.set(title)
	}

	private fun createAlarm() = Alarm(
			id, titleField.get()!!, messageField.get()!!, getDate(),
			0, soundUri, period, vibroField.get(), true)

	private fun getDate(): Date {
		val calendar = Calendar.getInstance().apply {
			val dayDelay = Alarm.getDaysToIncrement(get(Calendar.DAY_OF_WEEK), period)
			add(Calendar.DAY_OF_MONTH, dayDelay)
			set(Calendar.HOUR_OF_DAY, hoursField.get())
			set(Calendar.MINUTE, minutesField.get())
		}
		return calendar.time
	}

	fun onChoosePeriodClicked() { choosePeriodEvent.onNext(period) }

	fun onChooseSoundClicked() { chooseSoundEvent.onNext(soundUri) }

	fun onSwitchVibroClicked() { vibroField.set(!vibroField.get()) }

	fun onSubmitClicked() { submitAlarmEvent.onNext(createAlarm()) }

	fun onDeleteClicked() { deleteAlarmEvent.onNext(id) }

	fun choosePeriodEvent() = choosePeriodEvent as Observable<BooleanArray>

	fun chooseSoundEvent() = chooseSoundEvent as Observable<Uri>

	fun submitAlarmEvent() = submitAlarmEvent as Observable<Alarm>

	fun deleteAlarmEvent() = deleteAlarmEvent as Observable<Long>
}