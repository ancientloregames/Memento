package com.ancientlore.memento

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "alarms")
data class Alarm(@PrimaryKey(autoGenerate = true) var  id: Long = 0,
				 @field:ColumnInfo var title: String = "",
				 @field:ColumnInfo var date: Date)