package com.example.data

data class ExamConfig(
    val id: String,
    val name: String,
    val positiveMarks: Float,
    val negativeMarks: Float,
    val defaultDurationMinutes: Int,
    val defaultQuestions: Int,
    val subjects: List<String>,
    val description: String
) {
    companion object {
        val ALL_EXAMS = listOf(
            ExamConfig(
                id = "NEET",
                name = "NEET (Medical)",
                positiveMarks = 4.0f,
                negativeMarks = -1.0f,
                defaultDurationMinutes = 200,
                defaultQuestions = 200,
                subjects = listOf("Physics", "Chemistry", "Botany", "Zoology"),
                description = "National Eligibility cum Entrance Test. Single paper containing 200 multiple-choice questions."
            ),
            ExamConfig(
                id = "JEE",
                name = "JEE Mains (Engineering)",
                positiveMarks = 4.0f,
                negativeMarks = -1.0f,
                defaultDurationMinutes = 180,
                defaultQuestions = 90,
                subjects = listOf("Physics", "Chemistry", "Mathematics"),
                description = "Joint Entrance Examination. Computer/OMR based multiple-choice and numeric-value question grid."
            ),
            ExamConfig(
                id = "UPSC",
                name = "UPSC Prelims (GS-1)",
                positiveMarks = 2.0f,
                negativeMarks = -0.67f,
                defaultDurationMinutes = 120,
                defaultQuestions = 100,
                subjects = listOf("History", "Geography", "Polity", "Economy", "Sci-Tech", "Current Affairs"),
                description = "Civil Services Examination. General Studies Paper 1 containing 100 objective questions."
            ),
            ExamConfig(
                id = "CUET",
                name = "CUET (UG General)",
                positiveMarks = 5.0f,
                negativeMarks = -1.0f,
                defaultDurationMinutes = 60,
                defaultQuestions = 50,
                subjects = listOf("General Test", "Languages", "Domain subjects"),
                description = "Common University Entrance Test. Section III General Test with 50 MCQs (attempt 40)."
            ),
            ExamConfig(
                id = "NDA",
                name = "NDA (General Ability)",
                positiveMarks = 4.0f,
                negativeMarks = -1.33f,
                defaultDurationMinutes = 150,
                defaultQuestions = 150,
                subjects = listOf("English", "Physics", "Chemistry", "General Social Science"),
                description = "National Defence Academy entrance paper for General Ability Test of 150 questions."
            ),
            ExamConfig(
                id = "SSC",
                name = "SSC CGL (Tier 1)",
                positiveMarks = 2.0f,
                negativeMarks = -0.5f,
                defaultDurationMinutes = 60,
                defaultQuestions = 100,
                subjects = listOf("Reasoning", "General Awareness", "Quantitative Aptitude", "English"),
                description = "Staff Selection Commission. Combined Graduate Level online/mock MCQ exam."
            ),
            ExamConfig(
                id = "Banking",
                name = "IBPS PO / SBI PO",
                positiveMarks = 1.0f,
                negativeMarks = -0.25f,
                defaultDurationMinutes = 60,
                defaultQuestions = 100,
                subjects = listOf("English", "Quantitative Aptitude", "Reasoning Ability"),
                description = "Banking probationary services prelims. Modern speed-accuracy oriented mock."
            )
        )

        fun getById(id: String): ExamConfig {
            return ALL_EXAMS.find { it.id == id } ?: ALL_EXAMS.first()
        }
    }
}
