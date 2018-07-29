package com.ancientlore.memento

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.ancientlore.memento.databinding.ActivityAlarmBinding

class AlarmActivity: BaseActivity<ActivityAlarmBinding, AlarmActivityViewModel>() {

	companion object {
		const val EXTRA_ALARM = "alarm"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		viewModel.submitAlarmEvent()
				.take(1)
				.subscribe { submitAlarm(it) }
	}

	override fun getLayoutId() = R.layout.activity_alarm

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() : AlarmActivityViewModel {
		return intent.getParcelableExtra<Alarm>(EXTRA_ALARM)
				?.let { AlarmActivityViewModel(it) }
				?: AlarmActivityViewModel()
	}

	override fun getTitleId() = R.string.new_alarm

	private fun submitAlarm(alarm: Alarm) {
		val intent = Intent()
		intent.putExtra(EXTRA_ALARM, alarm)
		setResult(Activity.RESULT_OK, intent)
		finish()
	}
}