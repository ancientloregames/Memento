package com.ancientlore.memento

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import com.ancientlore.memento.databinding.ActivityNoticeBinding
import java.util.*

class NoticeActivity: BasicActivity<ActivityNoticeBinding, NoticeActivityViewModel>() {

	companion object {
		const val alarmDuration = 10000L
	}

	private var ringtone: Ringtone? = null
	private var vibrator: Vibrator? = null

	override fun getLayoutId() = R.layout.activity_notice

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = NoticeActivityViewModel()

	override fun getTitleId() = R.string.app_name

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		intent.getParcelableExtra<Alarm>(AlarmReceiver.EXTRA_ALARM)?.let {
			initViewModel(it)
			playSound(it.sound)
			if (it.withVibration) vibrate()
		}
	}

	private fun initViewModel(alarm: Alarm) {
		viewModel.titleField.set(alarm.title)
		viewModel.messageField.set(alarm.message)

		viewModel.onDismiss()
				.take(1)
				.subscribe { finish() }

		viewModel.onSnooze()
				.take(1)
				.subscribe { snooze(alarm) }
	}

	private fun playSound(uri: Uri) {
		ringtone = RingtoneManager.getRingtone(this, uri).apply { play() }
	}

	private fun vibrate() {
		((getSystemService(Context.VIBRATOR_SERVICE)) as Vibrator)
				.takeIf { it.hasVibrator() }?.run {
					vibrator = this
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && hasAmplitudeControl())
						vibrate(VibrationEffect.createOneShot(alarmDuration, VibrationEffect.DEFAULT_AMPLITUDE))
					else vibrate(alarmDuration)
				}
	}

	override fun finish() {
		ringtone?.stop()
		vibrator?.cancel()
		super.finish()
	}

	private fun snooze(alarm: Alarm) {
		Alarm(alarm).apply {
			date = getSnoozedDate(alarm.date, alarm.snooze)
			schedule(applicationContext)
		}
	}

	private fun getSnoozedDate(date: Date, snooze: Int) =
			Calendar.getInstance().apply {
				time = date
				add(Calendar.MINUTE, snooze)
			}.time
}