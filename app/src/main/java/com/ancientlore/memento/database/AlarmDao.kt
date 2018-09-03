package com.ancientlore.memento.database

import android.arch.persistence.room.*
import com.ancientlore.memento.Alarm

@Dao
interface AlarmDao {

	@Query("SELECT * FROM alarms")
	fun getAll(): List<Alarm>

	@Query("SELECT * FROM alarms WHERE id IN (:ids)")
	fun loadAllByIds(ids: LongArray): List<Alarm>

	@Query("SELECT * FROM alarms WHERE id LIKE :id")
	fun findById(id: Long): Alarm?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(word: Alarm): Long

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(word: List<Alarm>): LongArray

	@Update
	fun update(vararg word: Alarm)

	@Delete
	fun delete(word: Alarm)

	@Query("DELETE FROM alarms WHERE id = :id")
	fun deleteById(id: Long)
}