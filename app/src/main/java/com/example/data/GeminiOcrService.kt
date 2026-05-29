package com.example.data

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import com.example.BuildConfig

object GeminiOcrService {
    private const val TAG = "GeminiOcr"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    /**
     * Converts a bitmap to a base64 JPEG string.
     */
    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    /**
     * Connects to Gemini API to extract answer keys from the uploaded bitmap.
     * Retries or falls back if API Key is not found or fails.
     */
    suspend fun performOcr(bitmap: Bitmap): Map<Int, String>? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is not configured, falling back to local OCR engine.")
            return@withContext null
        }

        try {
            val base64Image = bitmapToBase64(bitmap)
            
            // Build direct JSON body for Gemini 3.5 Flash
            val prompt = """
                You are OMRify's high-precision edtech OCR engine. 
                Perform OCR on this answer-key image. Extract the question numbers and correct options (A, B, C, D or 1, 2, 3, 4).
                The image contains printed or handwritten answer keys like "1 - B", "2 (C)", "3:A", or just a sequential column of letters.
                
                You must return clean, raw JSON mapping each question number to its option letter (A, B, C, or D). 
                Format example: { "1": "B", "2": "C", "3": "A", "4": "D" }
                If the options are numbers (1,2,3,4), map 1->A, 2->B, 3->C, 4->D.
                Do not wrap the response in markdown blocks or write any explanation. Return ONLY the JSON object.
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("contents", org.json.JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", org.json.JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                            put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", "image/jpeg")
                                    put("data", base64Image)
                                })
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                })
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)
            
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.e(TAG, "Request failed: ${response.code} ${response.message}")
                return@withContext null
            }

            val responseBody = response.body?.string() ?: return@withContext null
            val responseObj = JSONObject(responseBody)
            val candidates = responseObj.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val textPart = parts?.optJSONObject(0)
            val responseText = textPart?.optString("text")?.trim() ?: return@withContext null

            Log.d(TAG, "Gemini Raw Response: $responseText")

            // Parse extracted json mapped
            val parsedMap = mutableMapOf<Int, String>()
            val innerJson = JSONObject(responseText)
            val keys = innerJson.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val qNum = key.toIntOrNull()
                val optionVal = innerJson.optString(key).trim().uppercase()
                if (qNum != null && optionVal.isNotEmpty() && optionVal in listOf("A", "B", "C", "D")) {
                    parsedMap[qNum] = optionVal
                }
            }

            if (parsedMap.isNotEmpty()) {
                return@withContext parsedMap
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error performing Gemini API OCR", e)
        }
        return@withContext null
    }
}
