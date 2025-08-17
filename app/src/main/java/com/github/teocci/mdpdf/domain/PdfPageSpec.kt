package com.github.teocci.mdpdf.domain

enum class MarginSize(val displayName: String, val points: Float) {
    COMPACT("Compact", 36f),
    NORMAL("Normal", 54f),
    WIDE("Wide", 72f)
}

enum class PageSize(val displayName: String, val widthPt: Float, val heightPt: Float) {
    A3("A3", 842f, 1191f),
    A4("A4", 595f, 842f),
    A5("A5", 420f, 595f),
    LETTER("Letter", 612f, 792f)
}

enum class Orientation(val displayName: String) {
    PORTRAIT("Portrait"),
    LANDSCAPE("Landscape")
}

data class PdfPageSpec(
    val marginSize: MarginSize = MarginSize.NORMAL,
    val pageSize: PageSize = PageSize.A4,
    val orientation: Orientation = Orientation.PORTRAIT
) {
    val effectiveWidth: Float
        get() = if (orientation == Orientation.PORTRAIT) pageSize.widthPt else pageSize.heightPt
    
    val effectiveHeight: Float
        get() = if (orientation == Orientation.PORTRAIT) pageSize.heightPt else pageSize.widthPt
    
    val contentWidth: Float
        get() = effectiveWidth - (marginSize.points * 2)
    
    val contentHeight: Float
        get() = effectiveHeight - (marginSize.points * 2)
}