package com.github.teocci.mdpdf.ui.convert

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.teocci.mdpdf.domain.MarginSize
import com.github.teocci.mdpdf.domain.Orientation
import com.github.teocci.mdpdf.domain.PageSize
import com.github.teocci.mdpdf.theme.LocalDesignSystem
import com.github.teocci.mdpdf.ui.components.SegmentedControl
import com.github.teocci.mdpdf.viewmodel.ConvertViewModel
import kotlinx.coroutines.launch

@Composable
fun ConvertScreen(
    viewModel: ConvertViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val designSystem = LocalDesignSystem.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val markdownPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setMarkdownFile(it, context) }
    }
    
    val outputPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        uri?.let { viewModel.setOutputUri(it) }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(designSystem.spacing.md.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(designSystem.spacing.md.dp)
            ) {
                // File Selection Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(designSystem.spacing.md.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (state.markdownFile == null) {
                            Button(
                                onClick = { markdownPicker.launch("text/*") },
                                modifier = Modifier.size(200.dp, 56.dp)
                            ) {
                                Icon(
                                    painter = painterResource(android.R.drawable.ic_menu_upload),
                                    contentDescription = "Select file",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Select Markdown")
                            }
                        } else {
                            Text(
                                text = state.fileName ?: "File selected",
                                style = MaterialTheme.typography.titleMedium
                            )
                            state.fileSize?.let { size ->
                                Text(
                                    text = formatFileSize(size),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(designSystem.spacing.sm.dp))
                            OutlinedButton(
                                onClick = { markdownPicker.launch("text/*") }
                            ) {
                                Text("Change File")
                            }
                        }
                    }
                }
                
                // Configuration Section
                if (state.markdownFile != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(designSystem.spacing.md.dp),
                            verticalArrangement = Arrangement.spacedBy(designSystem.spacing.md.dp)
                        ) {
                            // Margins
                            Text(
                                text = "Margins",
                                style = MaterialTheme.typography.titleSmall
                            )
                            SegmentedControl(
                                options = MarginSize.values().toList(),
                                selectedOption = state.pageSpec.marginSize,
                                onOptionSelected = { viewModel.updateMarginSize(it) },
                                optionLabel = { it.displayName }
                            )
                            
                            // Page Size
                            Text(
                                text = "Page Size",
                                style = MaterialTheme.typography.titleSmall
                            )
                            SegmentedControl(
                                options = PageSize.values().toList(),
                                selectedOption = state.pageSpec.pageSize,
                                onOptionSelected = { viewModel.updatePageSize(it) },
                                optionLabel = { it.displayName }
                            )
                            
                            // Orientation
                            Text(
                                text = "Orientation",
                                style = MaterialTheme.typography.titleSmall
                            )
                            SegmentedControl(
                                options = Orientation.values().toList(),
                                selectedOption = state.pageSpec.orientation,
                                onOptionSelected = { viewModel.updateOrientation(it) },
                                optionLabel = { it.displayName }
                            )
                        }
                    }
                    
                    // Output Location
                    if (state.customOutputUri != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(designSystem.spacing.sm.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Custom output location selected",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    
                    OutlinedButton(
                        onClick = {
                            val fileName = state.fileName?.replace(".md", ".pdf") ?: "output.pdf"
                            outputPicker.launch(fileName)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Change Output Location")
                    }
                    
                    // Convert Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.convertToPdf(context)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !state.isConverting
                    ) {
                        if (state.isConverting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Convert to PDF")
                        }
                    }
                }
            }
            
            // Show result messages
            state.resultMessage?.let { message ->
                coroutineScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = if (state.resultUri != null) "Open" else null
                    )
                    
                    // Clear the message after showing it
                    viewModel.clearResultMessage()
                    
                    // Handle snackbar action (Open button)
                    if (result == androidx.compose.material3.SnackbarResult.ActionPerformed && state.resultUri != null) {
                        try {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                setDataAndType(state.resultUri, "application/pdf")
                                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // If no PDF viewer available, show file manager
                            try {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                    setDataAndType(state.resultUri, "*/*")
                                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback: show in file manager
                                val intent = android.content.Intent(android.content.Intent.ACTION_GET_CONTENT).apply {
                                    type = "*/*"
                                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatFileSize(sizeInBytes: Long): String {
    return when {
        sizeInBytes < 1024 -> "$sizeInBytes B"
        sizeInBytes < 1024 * 1024 -> "%.1f KB".format(sizeInBytes / 1024.0)
        else -> "%.1f MB".format(sizeInBytes / (1024.0 * 1024.0))
    }
}