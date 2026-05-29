package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exam_attempts")
data class ExamAttempt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examType: String, // NEET, JEE, UPSC, CUET, NDA, UPSC, SSC, Banking, Railways, State PSC, etc.
    val timestamp: Long = System.currentTimeMillis(),
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unattemptedCount: Int,
    val finalMarks: Float,
    val maxMarks: Int,
    val accuracyPercentage: Float,
    val timeLimitSeconds: Int,
    val timeTakenSeconds: Int,
    val userAnswersJson: String, // e.g. {"1":"A","2":"B"}
    val correctAnswersJson: String, // e.g. {"1":"B","2":"C"}
    val chapterName: String = "",
    val subjectName: String = "",
    val notes: String = "",
    val mode: String = "Practice" // "Practice" or "Real Exam"
)
