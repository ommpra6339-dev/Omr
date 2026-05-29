package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamAttemptDao {
    @Query("SELECT * FROM exam_attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<ExamAttempt>>

    @Query("SELECT * FROM exam_attempts WHERE id = :id LIMIT 1")
    suspend fun getAttemptById(id: Long): ExamAttempt?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: ExamAttempt): Long

    @Query("DELETE FROM exam_attempts WHERE id = :id")
    suspend fun deleteAttemptById(id: Long)

    @Query("DELETE FROM exam_attempts")
    suspend fun clearAllAttempts()
}
