package com.ancientlore.memento

import android.app.Activity
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MainActivityViewModel(activity: Activity): BasicViewModel(activity), AlarmsListAdapter.Listener {

	private val addAlarmEvent = PublishSubject.create<Any>()

	private val alarmSelectedEvent = PublishSubject.create<Alarm>()

	private val alarmSwitchedEvent = PublishSubject.create<Pair<Alarm, Boolean>>()

	fun onAddAlarmClicked() { addAlarmEvent.onNext(Any()) }

	override fun onItemSelected(item: Alarm) { alarmSelectedEvent.onNext(item) }

	override fun onItemSwitched(item: Alarm, state: Boolean) { alarmSwitchedEvent.onNext(Pair(item, state)) }

	fun addAlarmEvent() = addAlarmEvent as Observable<Any>

	fun alarmSelectedEvent() = alarmSelectedEvent as Observable<Alarm>

	fun alarmSwitchedEvent() = alarmSwitchedEvent as Observable<Pair<Alarm, Boolean>>

	fun addNewAlarm() {
		startActivityForResult(AlarmActivity::class.java, MainActivity.INTENT_ADD_ALARM)
	}
}