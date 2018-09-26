package com.ancientlore.memento

import android.content.Context
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView

class AlarmsListAdapter(context: Context, items: MutableList<Alarm>):
		BasicListAdapter<Alarm, AlarmsListAdapter.ViewHolder, AlarmsListAdapter.Listener>(context, items) {

	interface Listener: BasicListAdapter.Listener<Alarm> {
		fun onItemSwitched(item: Alarm, state: Boolean)
	}

	override fun getViewHolderLayoutRes(viewType: Int) = R.layout.alarm_list_item

	override fun getViewHolder(layout: View) = ViewHolder(layout)

	override fun onBindViewHolder(holder: ViewHolder, index: Int) {
		super.onBindViewHolder(holder, index)

		val item = items[index]

		holder.listener = object : ViewHolder.Listener {
			override fun onSwitchClicked(checked: Boolean) {
				listener?.onItemSwitched(item, checked)
			}
		}
	}

	fun findItem(id: Long) = items.find { it.id == id }

	override fun compareItems(first: Alarm, second: Alarm) = first.id == second.id

	class ViewHolder(itemView: View): BasicListAdapter.ViewHolder<Alarm>(itemView) {

		interface Listener {
			fun onSwitchClicked(checked: Boolean)
		}
		var listener: Listener? = null

		private val titleView = itemView.findViewById<TextView>(R.id.title)
		private val subtitleView = itemView.findViewById<TextView>(R.id.subtitle)
		private val switchView = itemView.findViewById<CompoundButton>(R.id.switcher)

		override fun bind(data: Alarm) {
			titleView.text = data.title
			subtitleView.text = data.date.toString()
			switchView.isChecked = data.enabled

			switchView.setOnClickListener { listener?.onSwitchClicked(switchView.isChecked) }
		}
	}
}