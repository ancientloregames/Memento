package com.ancientlore.memento

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.content.Intent
import java.lang.ref.WeakReference

open class BasicViewModel(activity: Activity) : ViewModel() {

	protected val activityRef : WeakReference<Activity> = WeakReference(activity)

	fun startActivityForResult(clazz: Class<out Activity>, requestCode: Int) {
		activityRef.get()?.let { activity ->
			val intent = Intent(activity, clazz)
			activity.startActivityForResult(intent, requestCode)
		}
	}
}