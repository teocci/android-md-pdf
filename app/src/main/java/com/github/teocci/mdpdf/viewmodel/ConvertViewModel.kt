package com.github.teocci.mdpdf.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.teocci.mdpdf.domain.ConvertMarkdownToPdf
import com.github.teocci.mdpdf.domain.MarginSize
import com.github.teocci.mdpdf.domain.Orientation
import com.github.teocci.mdpdf.domain.PageSize
import com.github.teocci.mdpdf.domain.PdfPageSpec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConvertUiState(
    val markdownFile: Uri? = null,
    val fileName: String? = null,
    val fileSize: Long? = null,
    val customOutputUri: Uri? = null,
    val pageSpec: PdfPageSpec = PdfPageSpec(),
    val isConverting: Boolean = false,
    val resultMessage: String? = null,
    val resultUri: Uri? = null
)

class ConvertViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ConvertUiState())
    val uiState: StateFlow<ConvertUiState> = _uiState.asStateFlow()
    
    fun clearResultMessage() {
        _uiState.update { it.copy(resultMessage = null) }
    }
    
    fun setMarkdownFile(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val fileName = getFileName(uri, context)
                val fileSize = getFileSize(uri, context)
                
                _uiState.update {
                    it.copy(
                        markdownFile = uri,
                        fileName = fileName,
                        fileSize = fileSize,
                        resultMessage = null,
                        resultUri = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(resultMessage = "Error reading file: ${e.message}")
                }
            }
        }
    }
    
    fun setOutputUri(uri: Uri) {
        _uiState.update {
            it.copy(customOutputUri = uri)
        }
    }
    
    fun updateMarginSize(marginSize: MarginSize) {
        _uiState.update {
            it.copy(pageSpec = it.pageSpec.copy(marginSize = marginSize))
        }
    }
    
    fun updatePageSize(pageSize: PageSize) {
        _uiState.update {
            it.copy(pageSpec = it.pageSpec.copy(pageSize = pageSize))
        }
    }
    
    fun updateOrientation(orientation: Orientation) {
        _uiState.update {
            it.copy(pageSpec = it.pageSpec.copy(orientation = orientation))
        }
    }
    
    fun convertToPdf(context: Context) {
        val state = _uiState.value
        val markdownUri = state.markdownFile ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isConverting = true, resultMessage = null) }
            
            try {
                val outputUri = ConvertMarkdownToPdf().convert(
                    context = context,
                    markdownUri = markdownUri,
                    customOutputUri = state.customOutputUri,
                    pageSpec = state.pageSpec
                )
                
                _uiState.update {
                    it.copy(
                        isConverting = false,
                        resultMessage = "PDF saved successfully",
                        resultUri = outputUri
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isConverting = false,
                        resultMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }
    
    private fun getFileName(uri: Uri, context: Context): String? {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getFileSize(uri: Uri, context: Context): Long? {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                cursor.moveToFirst()
                cursor.getLong(sizeIndex)
            }
        } catch (e: Exception) {
            null
        }
    }
}