package com.ancientlore.memento

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import com.ancientlore.memento.databinding.ActivityNoticeBinding

class NoticeActivity: BaseActivity<ActivityNoticeBinding, NoticeActivityViewModel>() {

	private var ringtone: Ringtone? = null

	override fun getLayoutId() = R.layout.activity_notice

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = NoticeActivityViewModel()

	override fun getTitleId() = R.string.app_name

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		intent.getParcelableExtra<Alarm>(AlarmReceiver.EXTRA_ALARM)?.let {
			initViewModel(it)
			ringtone = RingtoneManager.getRingtone(this, it.sound).apply { play() }
		}
	}

	private fun initViewModel(alarm: Alarm) {
		viewModel.titleField.set(alarm.title)
		viewModel.messageField.set(alarm.message)

		viewModel.dismissAlarmEvent()
				.take(1)
				.subscribe { finish() }
	}

	override fun finish() {
		ringtone?.stop()
		super.finish()
	}
}