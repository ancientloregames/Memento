package com.ancientlore.memento

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.ancientlore.memento.databinding.ActivityAlarmBinding

class AlarmActivity: BaseActivity<ActivityAlarmBinding, AlarmActivityViewModel>() {

	companion object {
		const val EXTRA_ALARM = "alarm"
		const val EXTRA_DELETE_ALARM_ID = "delete_alarm_id"
	}

	override fun getLayoutId() = R.layout.activity_alarm

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() : AlarmActivityViewModel {
		return intent.getParcelableExtra<Alarm>(EXTRA_ALARM)
				?.let { AlarmActivityViewModel(it) }
				?: AlarmActivityViewModel()
	}

	override fun getTitleId() = R.string.new_alarm

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		viewModel.submitAlarmEvent()
				.take(1)
				.subscribe { submitAlarm(it) }

		viewModel.deleteAlarmEvent()
				.take(1)
				.subscribe { deleteAlarm(it) }
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.alarm_menu, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.miDelete -> viewModel.onDeleteClicked()
		}
		return true
	}

	private fun submitAlarm(alarm: Alarm) {
		val intent = Intent()
		intent.putExtra(EXTRA_ALARM, alarm)
		setResult(Activity.RESULT_OK, intent)
		finish()
	}

	private fun deleteAlarm(id: Long) {
		val intent = Intent()
		intent.putExtra(EXTRA_DELETE_ALARM_ID, id)
		setResult(Activity.RESULT_OK, intent)
		finish()
	}
}