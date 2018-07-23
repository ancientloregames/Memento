package com.ancientlore.memento

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.ancientlore.memento.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>() {

	companion object {
		const val INTNENT_ADD_ALARM = 101
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		viewModel.onAddAlarmEvent()
				.takeUntil(destroyEvent)
				.subscribe { addAlarmIntent() }
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		if (resultCode != Activity.RESULT_OK) return

		when (requestCode) {
			INTNENT_ADD_ALARM -> {}
		}
	}

	override fun getLayoutId() = R.layout.activity_main

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = MainActivityViewModel()

	override fun getTitleId() = R.string.app_name

	private fun addAlarmIntent() {
		val intent = Intent(this, AlarmActivity::class.java)
		startActivityForResult(intent, INTNENT_ADD_ALARM)
	}
}
