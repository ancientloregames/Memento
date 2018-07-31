package com.ancientlore.memento

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MainActivityViewModel: ViewModel(), AlarmsListAdapter.Listener {

	private val addAlarmEvent = PublishSubject.create<Any>()

	private val alarmSelectedEvent = PublishSubject.create<Alarm>()

	private val alarmSwitchedEvent = PublishSubject.create<Pair<Alarm, Boolean>>()

	fun onAddAlarmClicked() { addAlarmEvent.onNext(Any()) }

	override fun onItemSelected(item: Alarm) { alarmSelectedEvent.onNext(item) }

	override fun onItemSwitched(item: Alarm, state: Boolean) { alarmSwitchedEvent.onNext(Pair(item, state)) }

	fun addAlarmEvent() = addAlarmEvent as Observable<Any>

	fun alarmSelectedEvent() = alarmSelectedEvent as Observable<Alarm>

	fun alarmSwitchedEvent() = alarmSwitchedEvent as Observable<Pair<Alarm, Boolean>>
}