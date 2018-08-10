package com.ancientlore.memento

import android.os.Bundle
import com.ancientlore.memento.databinding.ActivityNoticeBinding

class NoticeActivity: BaseActivity<ActivityNoticeBinding, NoticeActivityViewModel>() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		viewModel.dismissAlarmEvent()
				.take(1)
				.subscribe { finish() }
	}

	override fun getLayoutId() = R.layout.activity_notice

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() : NoticeActivityViewModel {
		val title = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_TITLE) ?: ""
		val message = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_MESSAGE) ?: ""

		return NoticeActivityViewModel(title, message)
	}

	override fun getTitleId() = R.string.app_name
}