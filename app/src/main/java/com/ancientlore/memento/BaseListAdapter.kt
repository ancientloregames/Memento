package com.ancientlore.memento

import android.content.Context
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseListAdapter<P, T: BaseListAdapter.ViewHolder<P>>(context: Context, private val items: MutableList<P>):
		RecyclerView.Adapter<T>() {

	private val layoutInflater = LayoutInflater.from(context)

	abstract fun getViewHolderLayoutRes(viewType: Int): Int

	abstract fun getViewHolder(layout: View): T

	private fun getViewHolderLayout(parent: ViewGroup, layoutRes: Int) = layoutInflater.inflate(layoutRes, parent,false)

	override fun getItemCount() = items.count()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
		val layoutRes = getViewHolderLayoutRes(viewType)
		val layout = getViewHolderLayout(parent, layoutRes)
		return getViewHolder(layout)
	}

	override fun onBindViewHolder(holder: T, index: Int) {
		holder.bind(items[index])
	}

	@UiThread
	fun addItem(newItem: P) {
		items.add(newItem)
		notifyItemInserted(itemCount - 1)
	}

	abstract class ViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView), Bindable<T>
}