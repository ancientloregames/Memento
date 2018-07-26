package com.ancientlore.memento

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.ancientlore.memento.AlarmActivity.Companion.EXTRA_ALARM
import com.ancientlore.memento.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/* TODO alarmActivity with result
*  addition to AlarmManager
*  bottomBar implementation
*
* */
class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>() {

	companion object {
		const val INTNENT_ADD_ALARM = 101
	}

	private val dbExec: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "db_worker") }

	private val db by lazy { AlarmsDatabase.getInstance(this) }

	private val alarmManager by lazy { getSystemService(Context.ALARM_SERVICE) as AlarmManager }

	private lateinit var listAdapter: AlarmsListAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		dbExec.submit {
			listAdapter = AlarmsListAdapter(this, db.alarmDao().getAll().toMutableList())
			alarmListView.adapter = listAdapter
		}

		viewModel.onAddAlarmEvent()
				.takeUntil(destroyEvent)
				.subscribe { addAlarmIntent() }
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

	private fun addAlarmToDb(alarm: Alarm) {
		dbExec.submit { db.alarmDao().insert(alarm) }
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
}
