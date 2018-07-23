package com.ancientlore.memento

import android.arch.persistence.room.*

@Dao
interface AlarmDao {

	@Query("SELECT * FROM alarms")
	fun getAll(): List<Alarm>

	@Query("SELECT * FROM alarms WHERE id IN (:ids)")
	fun loadAllByIds(ids: IntArray): List<Alarm>

	@Query("SELECT * FROM alarms WHERE id LIKE :first")
	fun findById(first: String): Alarm

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(vararg word: Alarm)

	@Insert
	fun insertAll(vararg word: Alarm)

	@Update
	fun update(vararg word: Alarm)

	@Delete
	fun delete(word: Alarm)
}