package com.ancientlore.memento

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MainActivityViewModel: ViewModel(), BaseListAdapter.Listener<Alarm> {

	private val addAlarmEvent = PublishSubject.create<Any>()

	private val alarmSelectedEvent = PublishSubject.create<Alarm>()

	fun onAddAlarmClicked() { addAlarmEvent.onNext(Any()) }

	override fun onItemSelected(item: Alarm) { alarmSelectedEvent.onNext(item) }

	fun addAlarmEvent() = addAlarmEvent as Observable<Any>

	fun alarmSelectedEvent() = alarmSelectedEvent as Observable<Alarm>
}