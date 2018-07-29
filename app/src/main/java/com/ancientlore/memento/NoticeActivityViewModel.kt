package com.ancientlore.memento

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class NoticeActivityViewModel: ViewModel() {

	private val dismissAlarmEvent = PublishSubject.create<Any>()

	fun onDismissClicked() { dismissAlarmEvent.onNext(Any()) }

	fun dismissAlarmEvent() = dismissAlarmEvent as Observable<Any>
}