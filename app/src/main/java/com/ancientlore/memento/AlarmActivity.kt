package com.ancientlore.memento

import com.ancientlore.memento.databinding.ActivityAlarmBinding

class AlarmActivity: BaseActivity<ActivityAlarmBinding, AlarmActivityViewModel>() {

	override fun getLayoutId() = R.layout.activity_alarm

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = AlarmActivityViewModel()

	override fun getTitleId() = R.string.new_alarm
}