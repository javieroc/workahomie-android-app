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
