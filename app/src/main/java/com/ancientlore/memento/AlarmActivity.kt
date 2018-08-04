package com.ancientlore.memento

import android.app.Activity
import android.app.AlertDialog
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

	private lateinit var days: Array<String>

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

		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		days = arrayOf(getString(R.string.monday), getString(R.string.tuesday), getString(R.string.wednesday),
				getString(R.string.thursday), getString(R.string.friday), getString(R.string.saturday), getString(R.string.sunday))

		viewModel.choosePeriodEvent()
				.takeUntil(destroyEvent)
				.subscribe { choosePeriod(it) }

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
			android.R.id.home -> finish()
			R.id.miDelete -> viewModel.onDeleteClicked()
		}
		return true
	}

	private fun choosePeriod(prevChoice: BooleanArray) {
		val currentChoice = prevChoice.clone()

		AlertDialog.Builder(this)
				.setMultiChoiceItems(days, currentChoice) { _, which, isChecked -> currentChoice[which] = isChecked }
				.setPositiveButton(getString(R.string.done)) { _, _ ->  applyPeriod(currentChoice) }
				.create()
				.show()
	}

	private fun applyPeriod(period: BooleanArray) {
		viewModel.updatePeriod(period, getPeriodTitle(period))
	}

	private fun getPeriodTitle(period: BooleanArray) = when {
		period.all { it } -> getString(R.string.everyday)
		period.none { it } -> getString(R.string.onetime)
		else -> composeTitle(period)
	}

	private fun composeTitle(period: BooleanArray) : String {
		val  titleBuilder = StringBuilder()
		for (i in 0 until period.size) {
			if (period[i]) {
				titleBuilder.append(getDayByIndex(i))
				titleBuilder.append(' ')
			}
		}

		if (titleBuilder.isNotEmpty()) titleBuilder.deleteCharAt(titleBuilder.lastIndex)

		return titleBuilder.toString()
	}

	private fun getDayByIndex(index: Int) : String {
		return when (index) {
			0 -> getString(R.string.monday_short)
			1 -> getString(R.string.tuesday_short)
			2 -> getString(R.string.wednesday_short)
			3 -> getString(R.string.thursday_short)
			4 -> getString(R.string.friday_short)
			5 -> getString(R.string.saturday_short)
			6 -> getString(R.string.sunday_short)
			else -> ""
		}
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