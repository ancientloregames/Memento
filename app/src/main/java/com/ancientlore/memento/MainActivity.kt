package com.ancientlore.memento

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
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
		const val INTNENT_ADD_ALARM = 101
		const val INTNENT_MODIFY_ALARM = 102
	}

	private val dbExec: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "db_worker") }

	private val db by lazy { AlarmsDatabase.getInstance(this) }

	private val alarmManager by lazy { getSystemService(Context.ALARM_SERVICE) as AlarmManager }

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
			INTNENT_ADD_ALARM ->
				data?.let { it.getParcelableExtra<Alarm>(AlarmActivity.EXTRA_ALARM)
							?.let { addAlarm(it) } }
		}
	}

	override fun getLayoutId() = R.layout.activity_main

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = MainActivityViewModel()

	override fun getTitleId() = R.string.app_name

	private fun addAlarm(alarm: Alarm) {
		addAlarmToDb(alarm)
		scheduleAlarm(alarm)
		listAdapter.addItem(alarm)
	}

	private fun updateAlarm(alarm: Alarm) {
		updateAlarmInDb(alarm)
		runOnUiThread { listAdapter.updateItem(alarm) }
	}

	private fun addAlarmToDb(alarm: Alarm) {
		dbExec.submit { db.alarmDao().insert(alarm) }
	}

	private fun updateAlarmInDb(alarm: Alarm) {
		dbExec.submit { db.alarmDao().update(alarm) }
	}

	private fun scheduleAlarm(alarm: Alarm) {
		val intent = Intent(this, AlarmReceiver::class.java)
		intent.putExtra(EXTRA_ALARM, alarm)

		val pendingIntent =
				PendingIntent.getBroadcast(this, alarm.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
			alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.date.time, pendingIntent)
		else
			alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.date.time, pendingIntent)
	}

	private fun addAlarmIntent() {
		val intent = Intent(this, AlarmActivity::class.java)
		startActivityForResult(intent, INTNENT_ADD_ALARM)
	}

	private fun modifyAlarmIntent(alarm: Alarm) {
		val intent = Intent(this, AlarmActivity::class.java)
		intent.putExtra(EXTRA_ALARM, alarm)
		startActivityForResult(intent, INTNENT_MODIFY_ALARM)
	}

	private fun switchAlarmState(alarm: Alarm, isActive: Boolean) {
		alarm.active = isActive

		updateAlarm(alarm)

		when (isActive) {
			true -> {
			}
			else -> {
			}
		}
	}
}
