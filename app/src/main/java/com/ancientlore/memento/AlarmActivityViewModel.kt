package com.ancientlore.memento

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.net.Uri
import android.provider.Settings
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*

class AlarmActivityViewModel: ViewModel {

	val id: Long

	val titleField = ObservableField<String>("")
	val messageField = ObservableField<String>("")
	val soundField = ObservableField<String>("")

	/* FIXME two-way dataBinding doesn't work properly at this time. The binding of the minute attribute
	*  changes the hour filed in the View class https://issuetracker.google.com/issues/111948800
	*/
	val hoursField = ObservableInt(0)
	val minutesField = ObservableInt(0)

	val periodTitle = ObservableField<String>("")

	private var currentPeriod = BooleanArray(7) { false }
	private var currentSound = Settings.System.DEFAULT_ALARM_ALERT_URI

	private val choosePeriodEvent = PublishSubject.create<BooleanArray>()

	private val chooseSoundEvent = PublishSubject.create<Uri>()

	private val submitAlarmEvent = PublishSubject.create<Alarm>()

	private val deleteAlarmEvent = PublishSubject.create<Long>()

	constructor(periodTitle: String) {
		id = 0

		applyDate(null)

		this.periodTitle.set(periodTitle)
	}

	constructor(alarm: Alarm, periodTitle: String) {
		id = alarm.id
		titleField.set(alarm.title)
		messageField.set(alarm.message)
		currentSound = alarm.sound

		applyDate(alarm.date)

		updatePeriod(alarm.activeDays, periodTitle)
	}

	fun updatePeriod(newPeriod: BooleanArray, periodTitle: String) {
		currentPeriod = newPeriod
		this.periodTitle.set(periodTitle)
	}

	fun updateSound(title: String, sound: Uri) {
		currentSound = sound
		this.soundField.set(title)
	}

	fun onChoosePeriodClicked() { choosePeriodEvent.onNext(currentPeriod) }

	fun onChooseSoundClicked() { chooseSoundEvent.onNext(currentSound) }

	fun onSubmitClicked() { submitAlarmEvent.onNext(createAlarm()) }

	fun onDeleteClicked() { deleteAlarmEvent.onNext(id) }

	private fun createAlarm() = Alarm(
			id, titleField.get()!!, messageField.get()!!, getDate(),
			currentSound, currentPeriod, true)

	private fun applyDate(date: Date?) {
		val calendar = Calendar.getInstance()
		date?.let { calendar.time = it }
		hoursField.set(calendar.get(Calendar.HOUR_OF_DAY))
		minutesField.set(calendar.get(Calendar.MINUTE))
	}

	private fun getDate(): Date {
		val calendar = Calendar.getInstance().apply {
			val dayDelay = Alarm.getDaysToIncrement(get(Calendar.DAY_OF_WEEK), currentPeriod)
			add(Calendar.DAY_OF_MONTH, dayDelay)
			set(Calendar.HOUR_OF_DAY, hoursField.get())
			set(Calendar.MINUTE, minutesField.get())
		}
		return calendar.time
	}

	fun choosePeriodEvent() = choosePeriodEvent as Observable<BooleanArray>

	fun chooseSoundEvent() = chooseSoundEvent as Observable<Uri>

	fun submitAlarmEvent() = submitAlarmEvent as Observable<Alarm>

	fun deleteAlarmEvent() = deleteAlarmEvent as Observable<Long>
}