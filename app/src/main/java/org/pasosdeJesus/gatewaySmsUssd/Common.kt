package org.pasosdeJesus.gatewaySmsUssd

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

public fun addLog(logs: String, newMsg: String): String {
    val logsList = logs.split("\n").toTypedArray()
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    val currentDate = sdf.format(Date())
    val res = logsList.plus("[$currentDate] $newMsg")
    if (res.size > 5) {
        res[0] = ""
    }
    val res2 = res.filter { it != "" }.toTypedArray()
    return res2.joinToString(separator = "\n")
}

suspend fun fetchApiData(url: String): String {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()


        val response: Response = client.newCall(request).execute()
        if (response.isSuccessful) {
            var ret = response.body?.string() ?: "No data"
            response.close()
            ret
        } else {
            throw IOException("Unexpected code $response")
        }
    }
}

@Throws(IOException::class)
fun post(url: String, json: String): String {
    val client = OkHttpClient()
    val JSON: MediaType = "application/json".toMediaType()
    val body: RequestBody = json.toRequestBody(JSON)
    val request: Request = Request.Builder()
        .url(url)
        .post(body)
        .build()
    client.newCall(request).execute().use { response ->
        return response.body!!.string()
    }
}