package com.github.teocci.mdpdf.domain

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.github.teocci.mdpdf.pdf.PdfRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConvertMarkdownToPdf {
    
    suspend fun convert(
        context: Context,
        markdownUri: Uri,
        customOutputUri: Uri?,
        pageSpec: PdfPageSpec
    ): Uri = withContext(Dispatchers.IO) {
        // Read markdown content
        val markdownContent = context.contentResolver.openInputStream(markdownUri)?.use { stream ->
            stream.bufferedReader().readText()
        } ?: throw IllegalArgumentException("Unable to read markdown file")
        
        // Generate output filename if needed
        val outputFileName = getOutputFileName(markdownUri, context)
        
        // Render PDF
        val renderer = PdfRenderer(context, pageSpec)
        
        if (customOutputUri != null) {
            // User selected custom location
            context.contentResolver.openOutputStream(customOutputUri)?.use { outputStream ->
                renderer.renderMarkdownToPdf(markdownContent, outputStream)
            } ?: throw IllegalStateException("Unable to write to selected location")
            customOutputUri
        } else {
            // Save to Downloads folder
            saveToDownloads(context, outputFileName) { outputStream ->
                renderer.renderMarkdownToPdf(markdownContent, outputStream)
            }
        }
    }
    
    private fun getOutputFileName(markdownUri: Uri, context: Context): String {
        val originalName = try {
            context.contentResolver.query(markdownUri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            }
        } catch (e: Exception) {
            null
        }
        
        val baseName = originalName?.removeSuffix(".md")?.removeSuffix(".markdown") ?: "output"
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "${baseName}_$timestamp.pdf"
    }
    
    private fun saveToDownloads(
        context: Context,
        fileName: String,
        writeContent: (java.io.OutputStream) -> Unit
    ): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaStore for API 29+
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            
            val uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: throw IllegalStateException("Failed to create MediaStore entry")
            
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                writeContent(outputStream)
            } ?: throw IllegalStateException("Failed to open output stream")
            
            uri
        } else {
            // Use direct file access for older APIs
            @Suppress("DEPRECATION")
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            
            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use { outputStream ->
                writeContent(outputStream)
            }
            
            Uri.fromFile(file)
        }
    }
}