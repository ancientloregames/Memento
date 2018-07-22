package com.ancientlore.memento

import android.os.Bundle
import com.ancientlore.memento.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		viewModel.onAddAlarmEvent()
				.takeUntil(destroyEvent)
				.subscribe { addAlarmIntent() }
	}

	override fun getLayoutId() = R.layout.activity_main

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = MainActivityViewModel()

	override fun getTitleId() = R.string.app_name

	private fun addAlarmIntent() {
	}
}
