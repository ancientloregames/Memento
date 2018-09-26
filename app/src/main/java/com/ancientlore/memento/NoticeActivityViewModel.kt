package com.ancientlore.memento

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class NoticeActivityViewModel(activity: Activity): BasicViewModel(activity) {

	val titleField = ObservableField<String>("")
	val messageField = ObservableField<String>("")

	private val dismissEvent = PublishSubject.create<Any>()
	private val snoozeEvent = PublishSubject.create<Any>()

	fun onDismissClicked() { dismissEvent.onNext(Any()) }

	fun onSnoozeClicked() { snoozeEvent.onNext(Any()) }

	fun onDismiss() = dismissEvent as Observable<Any>

	fun onSnooze() = snoozeEvent as Observable<Any>
}