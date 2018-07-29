package com.ancientlore.memento

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.databinding.ObservableInt
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*


class AlarmActivityViewModel: ViewModel() {

	val title = ObservableField<String>("")

	val hours = ObservableInt(0)
	val minutes = ObservableInt(0)

	private val submitAlarmEvent = PublishSubject.create<Alarm>()

	fun onSubmitClicked() { submitAlarmEvent.onNext(createAlarm()) }

	fun submitAlarmEvent() = submitAlarmEvent as Observable<Alarm>

	private fun createAlarm() = Alarm(0, title.get()!!, getDate())

	private fun getDate(): Date {
		val presentTime = Calendar.getInstance()
		val calendar = GregorianCalendar()
		calendar.set(presentTime.get(Calendar.YEAR), presentTime.get(Calendar.MONTH), presentTime.get(Calendar.DATE),
				hours.get(), minutes.get(), 0)
		return calendar.time
	}
}