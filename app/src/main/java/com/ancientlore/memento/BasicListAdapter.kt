package com.ancientlore.memento

import android.content.Context
import android.support.annotation.CallSuper
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BasicListAdapter<
		P,
		T: BasicListAdapter.ViewHolder<P>,
		F: BasicListAdapter.Listener<P>>(context: Context, internal val items: MutableList<P>):
		RecyclerView.Adapter<T>(), MutableAdapter<P> {

	interface Listener<P> {
		fun onItemSelected(item: P)
	}
	var listener: F? = null

	private val layoutInflater = LayoutInflater.from(context)

	abstract fun getViewHolderLayoutRes(viewType: Int): Int

	abstract fun getViewHolder(layout: View): T

	abstract fun compareItems(first: P, second: P) : Boolean

	abstract fun isUnique(item: P) : Boolean

	private fun getViewHolderLayout(parent: ViewGroup, layoutRes: Int) = layoutInflater.inflate(layoutRes, parent,false)

	override fun getItemCount() = items.count()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
		val layoutRes = getViewHolderLayoutRes(viewType)
		val layout = getViewHolderLayout(parent, layoutRes)
		return getViewHolder(layout)
	}

	@CallSuper
	override fun onBindViewHolder(holder: T, index: Int) {
		val item = items[index]
		holder.bind(items[index])
		holder.onClick(Runnable {
			listener?.onItemSelected(item)
		})
	}

	@UiThread
	override fun setItems(newItems: List<P>) {
		items.clear()
		items.addAll(newItems)
		notifyDataSetChanged()
	}

	@UiThread
	override fun addItem(newItem: P): Boolean {
		val allowAddition = isUnique(newItem)
		if (allowAddition) {
			items.add(newItem)
			notifyItemInserted(itemCount - 1)
		}

		return allowAddition
	}

	@UiThread
	override fun updateItem(updatedItem: P): Boolean {
		return items.indexOfFirst { compareItems(it, updatedItem) }
				.takeIf { it != -1 }
				?.let {
					updateItemAt(it, updatedItem)
					true
				}
				?: false
	}

	@UiThread
	override fun deleteItem(itemToDelete: P): Boolean {
		val index = items.indexOf(itemToDelete)
		if (index != -1) {
			items.removeAt(index)
			notifyItemRemoved(index)
			return true
		}
		return false
	}

	private fun updateItemAt(index: Int, updatedItem: P) {
		items[index] = updatedItem
		notifyItemChanged(index)
	}

	abstract class ViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView), Bindable<T>, Clickable {

		override fun onClick(action: Runnable) { itemView.setOnClickListener { action.run() } }
	}
}