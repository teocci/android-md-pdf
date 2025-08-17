package com.github.teocci.mdpdf.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T> SegmentedControl(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    optionLabel: (T) -> String = { it.toString() }
) {
    Row(modifier = modifier) {
        options.forEachIndexed { index, option ->
            val isSelected = option == selectedOption
            val shape = when (index) {
                0 -> RoundedCornerShape(
                    topStart = 8.dp,
                    bottomStart = 8.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                )
                options.lastIndex -> RoundedCornerShape(
                    topStart = 0.dp,
                    bottomStart = 0.dp,
                    topEnd = 8.dp,
                    bottomEnd = 8.dp
                )
                else -> RoundedCornerShape(0.dp)
            }
            
            if (isSelected) {
                Button(
                    onClick = { },
                    shape = shape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = optionLabel(option),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            } else {
                OutlinedButton(
                    onClick = { onOptionSelected(option) },
                    shape = shape,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = optionLabel(option),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}