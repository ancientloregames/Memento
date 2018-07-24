package com.ancientlore.memento

import android.content.Context
import android.view.View
import android.widget.TextView

class AlarmsListAdapter(context: Context, items: MutableList<Alarm>):
		BaseListAdapter<Alarm, AlarmsListAdapter.ViewHolder>(context, items) {

	override fun getViewHolderLayoutRes(viewType: Int) = R.layout.alarm_list_item

	override fun getViewHolder(layout: View) = ViewHolder(layout)

	class ViewHolder(itemView: View): BaseListAdapter.ViewHolder<Alarm>(itemView) {

		private val titleView = itemView.findViewById<TextView>(R.id.title)
		private val subtitleView = itemView.findViewById<TextView>(R.id.subtitle)

		override fun bind(data: Alarm) {
			titleView.text = data.title
			subtitleView.text = data.date.toString()
		}
	}
}