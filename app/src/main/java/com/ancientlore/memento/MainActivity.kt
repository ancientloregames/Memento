package com.ancientlore.memento

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.ancientlore.memento.AlarmActivity.Companion.EXTRA_ALARM
import com.ancientlore.memento.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/* TODO alarmActivity with result
*  bottomBar implementation
*
* */
class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>() {

	companion object {
		const val INTENT_ADD_ALARM = 101
		const val INTENT_MODIFY_ALARM = 102
	}

	private val dbExec: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "db_worker") }

	private val db by lazy { AlarmsDatabase.getInstance(this) }

	private lateinit var listAdapter: AlarmsListAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		alarmListView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

		dbExec.submit {
			listAdapter = AlarmsListAdapter(this, db.alarmDao().getAll().toMutableList())
			listAdapter.listener = viewModel
			alarmListView.adapter = listAdapter
		}

		viewModel.addAlarmEvent()
				.takeUntil(destroyEvent)
				.subscribe { addAlarmIntent() }

		viewModel.alarmSelectedEvent()
				.takeUntil(destroyEvent)
				.subscribe { modifyAlarmIntent(it) }

		viewModel.alarmSwitchedEvent()
				.takeUntil(destroyEvent)
				.subscribe { switchAlarmState(it.first, it.second) }
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		if (resultCode != Activity.RESULT_OK) return

		when (requestCode) {
			INTENT_ADD_ALARM ->
				data?.getParcelableExtra<Alarm>(AlarmActivity.EXTRA_ALARM)?.let {
					addAlarm(it) }
			INTENT_MODIFY_ALARM ->
				data?.getLongExtra(AlarmActivity.EXTRA_DELETE_ALARM_ID, -1).takeIf { it != -1L }?.let {
					deleteAlarm(it) }
		}
	}

	override fun getLayoutId() = R.layout.activity_main

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = MainActivityViewModel()

	override fun getTitleId() = R.string.app_name

	private fun addAlarm(alarm: Alarm) {
		addAlarmToDb(alarm)
		AlarmReceiver.scheduleAlarm(this, alarm)
		runOnUiThread { listAdapter.addItem(alarm) }
	}

	private fun updateAlarm(alarm: Alarm) {
		updateAlarmInDb(alarm)
		runOnUiThread { listAdapter.updateItem(alarm) }
	}

	private fun deleteAlarm(id: Long) {
		listAdapter.findItem(id)?.let {
			runOnUiThread { listAdapter.deleteItem(it) }
			AlarmReceiver.cancelAlarm(this, it)
			deleteAlarmInDb(it)
		}
	}

	private fun addAlarmToDb(alarm: Alarm) {
		dbExec.submit { db.alarmDao().insert(alarm) }
	}

	private fun updateAlarmInDb(alarm: Alarm) {
		dbExec.submit { db.alarmDao().update(alarm) }
	}

	private fun deleteAlarmInDb(alarm: Alarm) {
		dbExec.submit { db.alarmDao().delete(alarm) }
	}

	private fun addAlarmIntent() {
		val intent = Intent(this, AlarmActivity::class.java)
		startActivityForResult(intent, INTENT_ADD_ALARM)
	}

	private fun modifyAlarmIntent(alarm: Alarm) {
		val intent = Intent(this, AlarmActivity::class.java)
		intent.putExtra(EXTRA_ALARM, alarm)
		startActivityForResult(intent, INTENT_MODIFY_ALARM)
	}

	private fun switchAlarmState(alarm: Alarm, isActive: Boolean) {
		alarm.active = isActive

		updateAlarm(alarm)

		when (isActive) {
			true -> AlarmReceiver.scheduleAlarm(this, alarm)
			else -> AlarmReceiver.cancelAlarm(this, alarm)
		}
	}
}
