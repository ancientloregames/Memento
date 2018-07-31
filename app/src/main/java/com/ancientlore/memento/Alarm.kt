package com.ancientlore.memento

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@Entity(tableName = "alarms")
data class Alarm(@PrimaryKey(autoGenerate = true) var  id: Long = 0,
				 @field:ColumnInfo var title: String = "",
				 @field:ColumnInfo var date: Date,
				 @field:ColumnInfo var active: Boolean): Parcelable {

	constructor(parcel: Parcel) : this(
			parcel.readLong(),
			parcel.readString(),
			Date(parcel.readLong()),
			parcel.readInt() != 0)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeLong(id)
		parcel.writeString(title)
		parcel.writeLong(date.time)
		parcel.writeInt(if (active) 1 else 0)
	}

	override fun describeContents() = 0

	companion object CREATOR : Parcelable.Creator<Alarm> {

		override fun createFromParcel(parcel: Parcel) = Alarm(parcel)

		override fun newArray(size: Int) = arrayOfNulls<Alarm>(size)
	}
}