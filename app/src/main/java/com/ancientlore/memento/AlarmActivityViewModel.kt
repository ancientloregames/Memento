package com.ancientlore.memento

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.databinding.ObservableInt
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*


class AlarmActivityViewModel: ViewModel {

	val id: Long

	val title = ObservableField<String>("")

	/* FIXME two-way dataBinding doesn't work properly at this time. The binding of the minute attribute
	*  changes the hour filed in the View class https://issuetracker.google.com/issues/111948800
	*/
	val hours = ObservableInt(0)
	val minutes = ObservableInt(0)

	private val submitAlarmEvent = PublishSubject.create<Alarm>()

	private val deleteAlarmEvent = PublishSubject.create<Long>()

	constructor() {
		id = 0

		applyDate(null)
	}

	constructor(alarm: Alarm) {
		id = alarm.id
		title.set(alarm.title)

		applyDate(alarm.date)
	}

	fun onSubmitClicked() { submitAlarmEvent.onNext(createAlarm()) }

	fun onDeleteClicked() { deleteAlarmEvent.onNext(id) }

	private fun createAlarm() = Alarm(id, title.get()!!, getDate(), true)

	private fun applyDate(date: Date?) {
		val calendar = Calendar.getInstance()
		date?.let { calendar.time = it }
		hours.set(calendar.get(Calendar.HOUR_OF_DAY))
		minutes.set(calendar.get(Calendar.MINUTE))
	}

	private fun getDate(): Date {
		val calendar = Calendar.getInstance()
		calendar.set(Calendar.HOUR_OF_DAY, hours.get())
		calendar.set(Calendar.MINUTE, minutes.get())
		return calendar.time
	}

	fun submitAlarmEvent() = submitAlarmEvent as Observable<Alarm>

	fun deleteAlarmEvent() = deleteAlarmEvent as Observable<Long>
}