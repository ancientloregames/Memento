package com.ancientlore.memento

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.ancientlore.memento.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>() {

	companion object {
		const val INTNENT_ADD_ALARM = 101
	}

	private val dbExec: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "db_worker") }

	private val db by lazy { AlarmsDatabase.getInstance(this) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		dbExec.submit { alarmListView.adapter = AlarmsListAdapter(this, db.alarmDao().getAll().toMutableList()) }

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
