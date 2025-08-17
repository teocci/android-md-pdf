package com.github.teocci.mdpdf.pdf

object LayoutUtils {
    // Typography point sizes as per requirements
    const val H1_SIZE_PT = 14f
    const val H2_SIZE_PT = 12f
    const val H3_SIZE_PT = 11f
    const val H4_SIZE_PT = 10f
    const val BODY_SIZE_PT = 10f
    const val CODE_SIZE_PT = 10f
    
    // Spacing
    const val PARAGRAPH_SPACING_PT = 8f
    const val HEADING_TOP_SPACING_PT = 16f
    const val HEADING_BOTTOM_SPACING_PT = 8f
    const val LIST_INDENT_PT = 24f
    const val LIST_ITEM_SPACING_PT = 4f
    const val CODE_BLOCK_PADDING_PT = 12f
    const val CODE_BLOCK_RADIUS_PT = 4f
    
    // Convert points to pixels (Android uses 72 DPI)
    fun ptToPx(pt: Float): Float = pt * 72f / 72f // 1:1 for 72 DPI
    
    // Calculate text height for layout
    fun calculateTextHeight(text: String, width: Float, paint: android.graphics.Paint): Float {
        val layout = android.text.StaticLayout.Builder.obtain(
            text, 0, text.length, android.text.TextPaint(paint), width.toInt()
        ).build()
        return layout.height.toFloat()
    }
    
    // Split text into lines that fit within width
    fun splitTextToFitWidth(text: String, width: Float, paint: android.graphics.Paint): List<String> {
        val lines = mutableListOf<String>()
        val words = text.split(" ")
        var currentLine = ""
        
        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val testWidth = paint.measureText(testLine)
            
            if (testWidth > width && currentLine.isNotEmpty()) {
                lines.add(currentLine)
                currentLine = word
            } else {
                currentLine = testLine
            }
        }
        
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }
        
        return lines
    }
}