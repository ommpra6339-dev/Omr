package com.example.data

object OcrEngine {

    data class SampleDoc(
        val name: String,
        val examType: String,
        val description: String,
        val defaultQuestionCount: Int,
        val correctAnswers: Map<Int, String>
    )

    val SAMPLE_DOCS = listOf(
        SampleDoc(
            name = "NEET Biology Mock (Selected PYQs)",
            examType = "NEET",
            description = "Selected Botany & Zoology questions from past 5 years. Ideal for checking speed-accuracy.",
            defaultQuestionCount = 100,
            correctAnswers = mapOf(
                1 to "A", 2 to "C", 3 to "B", 4 to "D", 5 to "A",
                6 to "C", 7 to "C", 8 to "B", 9 to "A", 10 to "D",
                11 to "B", 12 to "D", 13 to "C", 14 to "A", 15 to "C",
                16 to "B", 17 to "A", 18 to "D", 19 to "D", 20 to "B",
                21 to "C", 22 to "A", 23 to "C", 24 to "B", 25 to "A",
                26 to "D", 27 to "C", 28 to "B", 29 to "D", 30 to "A",
                31 to "A", 32 to "C", 33 to "B", 34 to "D", 35 to "A",
                36 to "C", 37 to "C", 38 to "B", 39 to "A", 40 to "D",
                41 to "B", 42 to "D", 43 to "C", 44 to "A", 45 to "C",
                46 to "B", 47 to "A", 48 to "D", 49 to "D", 50 to "B",
                51 to "C", 52 to "A", 53 to "C", 54 to "B", 55 to "A",
                56 to "D", 57 to "C", 58 to "B", 59 to "D", 60 to "A",
                61 to "A", 62 to "C", 63 to "A", 64 to "B", 65 to "C",
                66 to "D", 67 to "C", 68 to "B", 69 to "A", 70 to "C",
                71 to "B", 72 to "D", 73 to "C", 74 to "A", 75 to "C",
                76 to "B", 77 to "A", 78 to "D", 79 to "D", 80 to "B",
                81 to "C", 82 to "A", 83 to "C", 84 to "B", 85 to "A",
                86 to "D", 87 to "C", 88 to "B", 89 to "D", 90 to "A",
                91 to "C", 92 to "D", 93 to "B", 94 to "A", 95 to "A",
                96 to "B", 97 to "C", 98 to "D", 99 to "C", 100 to "B"
            )
        ),
        SampleDoc(
            name = "JEE Main Physics 2025 Test Part-A",
            examType = "JEE",
            description = "Section A (Single Option Correct) with conceptual thermodynamics and mechanics PYQs.",
            defaultQuestionCount = 30,
            correctAnswers = mapOf(
                1 to "C", 2 to "A", 3 to "D", 4 to "B", 5 to "C",
                6 to "D", 7 to "A", 8 to "B", 9 to "C", 10 to "A",
                11 to "D", 12 to "D", 13 to "C", 14 to "B", 15 to "A",
                16 to "A", 17 to "C", 18 to "B", 19 to "D", 20 to "B",
                21 to "B", 22 to "C", 23 to "A", 24 to "B", 25 to "D",
                26 to "C", 27 to "A", 28 to "C", 29 to "D", 30 to "B"
            )
        ),
        SampleDoc(
            name = "UPSC Prelims GS-Paper-1 PYQ Key",
            examType = "UPSC",
            description = "GS Paper-1 general paper key covering Indian History & Constitution.",
            defaultQuestionCount = 50,
            correctAnswers = mapOf(
                1 to "A", 2 to "B", 3 to "C", 4 to "D", 5 to "A",
                6 to "B", 7 to "C", 8 to "D", 9 to "A", 10 to "B",
                11 to "C", 12 to "D", 13 to "A", 14 to "B", 15 to "C",
                16 to "D", 17 to "A", 18 to "B", 19 to "C", 20 to "D",
                21 to "A", 22 to "B", 23 to "C", 24 to "D", 25 to "A",
                26 to "B", 27 to "C", 28 to "D", 29 to "A", 30 to "B",
                31 to "C", 32 to "D", 33 to "A", 34 to "B", 35 to "C",
                36 to "D", 37 to "A", 38 to "B", 39 to "C", 40 to "D",
                41 to "A", 42 to "B", 43 to "C", 44 to "D", 45 to "A",
                46 to "B", 47 to "C", 48 to "D", 49 to "A", 50 to "B"
            )
        )
    )

    /**
     * Generates a realistic set of parsed options if scanning a random custom document.
     */
    fun generateSimulatedParsedKeys(examType: String, totalQuestions: Int): Map<Int, String> {
        val options = listOf("A", "B", "C", "D")
        val parsed = mutableMapOf<Int, String>()
        for (i in 1..totalQuestions) {
            // Generate deterministic but pseudo-random keys for simulation fun
            val index = (i * 3 + 7) % 4
            parsed[i] = options[index]
        }
        return parsed
    }
}
