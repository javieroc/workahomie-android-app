package com.app.workahomie.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import org.json.JSONObject


fun String.toRequestBodyPart(): RequestBody =
    this.toRequestBody("text/plain".toMediaTypeOrNull())

fun File.toMultipartBodyPart(fieldName: String): MultipartBody.Part =
    MultipartBody.Part.createFormData(fieldName, name, asRequestBody("image/*".toMediaTypeOrNull()))

fun uriToFile(context: Context, uri: Uri, fileName: String = "temp_upload"): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Cannot open URI: $uri")
    val file = File(context.cacheDir, fileName)
    FileOutputStream(file).use { output ->
        inputStream.copyTo(output)
    }
    return file
}

data class ParsedAddress(
    val displayName: String,
    val rawJson: String
)

fun parseAddress(address: String?): ParsedAddress {
    if (address.isNullOrBlank()) return ParsedAddress("", "")

    return try {
        val json = JSONObject(address)
        val displayName = when {
            json.has("display_name") -> json.getString("display_name")
            json.has("address") -> json.getString("address")
            json.has("name") -> json.getString("name")
            else -> address
        }
        ParsedAddress(displayName = displayName, rawJson = address)
    } catch (e: Exception) {
        ParsedAddress(displayName = address, rawJson = address)
    }
}
