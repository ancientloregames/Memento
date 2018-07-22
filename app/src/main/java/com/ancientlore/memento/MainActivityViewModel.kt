package com.ancientlore.memento

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MainActivityViewModel: ViewModel() {

	private val addAlarmEvent = PublishSubject.create<Any>()

	fun onAddAlarmClicked() {
		addAlarmEvent.onNext(Any())
	}

	fun onAddAlarmEvent() = addAlarmEvent as Observable<Any>
}