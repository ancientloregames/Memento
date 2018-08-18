package com.ancientlore.memento

import android.arch.persistence.room.*

@Dao
interface AlarmDao {

	@Query("SELECT * FROM alarms")
	fun getAll(): List<Alarm>

	@Query("SELECT * FROM alarms WHERE id IN (:ids)")
	fun loadAllByIds(ids: LongArray): List<Alarm>

	@Query("SELECT * FROM alarms WHERE id LIKE :first")
	fun findById(first: Long): Alarm?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(word: Alarm): Long

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(word: List<Alarm>): LongArray

	@Update
	fun update(vararg word: Alarm)

	@Delete
	fun delete(word: Alarm)
}