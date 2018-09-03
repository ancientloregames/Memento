package com.ancientlore.memento.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.ancientlore.memento.Alarm
import com.ancientlore.memento.SingletonHolder

@Database(entities = [(Alarm::class)], version = 1)
@TypeConverters(DataConverters::class)
abstract class AlarmsDatabase : RoomDatabase() {

	abstract fun alarmDao(): AlarmDao

	companion object : SingletonHolder<AlarmsDatabase, Context>({
		Room.databaseBuilder(it, AlarmsDatabase::class.java, "alarms.db").build()
	})
}