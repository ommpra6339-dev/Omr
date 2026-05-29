package com.example.data

import kotlinx.coroutines.flow.Flow

class ExamRepository(private val dao: ExamAttemptDao) {
    val allAttempts: Flow<List<ExamAttempt>> = dao.getAllAttempts()

    suspend fun getAttemptById(id: Long): ExamAttempt? {
        return dao.getAttemptById(id)
    }

    suspend fun insertAttempt(attempt: ExamAttempt): Long {
        return dao.insertAttempt(attempt)
    }

    suspend fun deleteAttempt(id: Long) {
        dao.deleteAttemptById(id)
    }

    suspend fun clearAll() {
        dao.clearAllAttempts()
    }
}
