package com.github.teocci.mdpdf.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.github.teocci.mdpdf.domain.PdfPageSpec
import org.commonmark.node.*
import org.commonmark.parser.Parser
import java.io.OutputStream

class PdfRenderer(
    private val context: Context,
    private val pageSpec: PdfPageSpec
) {
    private val fonts = Fonts(context)
    private val parser = Parser.builder().build()
    
    private var currentY = 0f
    private var currentPage: PdfDocument.Page? = null
    private var currentCanvas: Canvas? = null
    private var pageNumber = 0
    
    fun renderMarkdownToPdf(markdownContent: String, outputStream: OutputStream) {
        val document = PdfDocument()
        
        try {
            val ast = parser.parse(markdownContent)
            
            startNewPage(document)
            
            // Walk through AST and render each node
            ast.accept(object : AbstractVisitor() {
                override fun visit(heading: Heading) {
                    renderHeading(heading, document)
                }
                
                override fun visit(paragraph: Paragraph) {
                    renderParagraph(paragraph, document)
                }
                
                override fun visit(blockQuote: BlockQuote) {
                    renderBlockQuote(blockQuote, document)
                }
                
                override fun visit(bulletList: BulletList) {
                    renderBulletList(bulletList, document)
                }
                
                override fun visit(orderedList: OrderedList) {
                    renderOrderedList(orderedList, document)
                }
                
                override fun visit(code: FencedCodeBlock) {
                    renderCodeBlock(code, document)
                }
                
                override fun visit(code: IndentedCodeBlock) {
                    renderCodeBlock(code, document)
                }
                
                override fun visit(rule: ThematicBreak) {
                    renderHorizontalRule(document)
                }
            })
            
            finishCurrentPage(document)
            document.writeTo(outputStream)
        } finally {
            document.close()
        }
    }
    
    private fun startNewPage(document: PdfDocument) {
        finishCurrentPage(document)
        
        val pageInfo = PdfDocument.PageInfo.Builder(
            pageSpec.effectiveWidth.toInt(),
            pageSpec.effectiveHeight.toInt(),
            ++pageNumber
        ).create()
        
        currentPage = document.startPage(pageInfo)
        currentCanvas = currentPage?.canvas
        currentY = pageSpec.marginSize.points
    }
    
    private fun finishCurrentPage(document: PdfDocument) {
        currentPage?.let { document.finishPage(it) }
        currentPage = null
        currentCanvas = null
    }
    
    private fun renderHeading(heading: Heading, document: PdfDocument) {
        val level = heading.level
        val text = extractText(heading)
        
        val (sizePt, topSpacing) = when (level) {
            1 -> LayoutUtils.H1_SIZE_PT to LayoutUtils.HEADING_TOP_SPACING_PT
            2 -> LayoutUtils.H2_SIZE_PT to LayoutUtils.HEADING_TOP_SPACING_PT
            3 -> LayoutUtils.H3_SIZE_PT to LayoutUtils.HEADING_TOP_SPACING_PT
            4 -> LayoutUtils.H4_SIZE_PT to LayoutUtils.HEADING_TOP_SPACING_PT
            else -> LayoutUtils.BODY_SIZE_PT to LayoutUtils.PARAGRAPH_SPACING_PT
        }
        
        val paint = fonts.getTitlePaint(sizePt, bold = true)
        val height = calculateTextHeight(text, paint)
        
        ensureSpace(height + topSpacing + LayoutUtils.HEADING_BOTTOM_SPACING_PT, document)
        
        currentY += topSpacing
        drawText(text, paint)
        currentY += LayoutUtils.HEADING_BOTTOM_SPACING_PT
    }
    
    private fun renderParagraph(paragraph: Paragraph, document: PdfDocument) {
        val text = extractText(paragraph)
        val paint = fonts.getBodyPaint()
        val height = calculateTextHeight(text, paint)
        
        ensureSpace(height + LayoutUtils.PARAGRAPH_SPACING_PT, document)
        
        drawText(text, paint)
        currentY += LayoutUtils.PARAGRAPH_SPACING_PT
    }
    
    private fun renderBlockQuote(blockQuote: BlockQuote, document: PdfDocument) {
        val text = extractText(blockQuote)
        val paint = fonts.getBodyPaint()
        val linePaint = fonts.getLinePaint(Color.GRAY, 2f)
        
        val height = calculateTextHeight(text, paint)
        ensureSpace(height + LayoutUtils.PARAGRAPH_SPACING_PT, document)
        
        // Draw quote line
        currentCanvas?.drawLine(
            pageSpec.marginSize.points + 8f,
            currentY,
            pageSpec.marginSize.points + 8f,
            currentY + height,
            linePaint
        )
        
        // Draw text with indent
        val savedMargin = pageSpec.marginSize.points
        drawTextWithOffset(text, paint, 24f)
        currentY += LayoutUtils.PARAGRAPH_SPACING_PT
    }
    
    private fun renderBulletList(list: BulletList, document: PdfDocument) {
        renderList(list, document) { index -> "•" }
    }
    
    private fun renderOrderedList(list: OrderedList, document: PdfDocument) {
        var itemNumber = list.markerStartNumber
        renderList(list, document) { index -> "${itemNumber++}." }
    }
    
    private fun renderList(list: Node, document: PdfDocument, getMarker: (Int) -> String) {
        var index = 0
        var child = list.firstChild
        
        while (child != null) {
            if (child is ListItem) {
                renderListItem(child, getMarker(index++), document)
            }
            child = child.next
        }
        
        currentY += LayoutUtils.LIST_ITEM_SPACING_PT
    }
    
    private fun renderListItem(item: ListItem, marker: String, document: PdfDocument) {
        val text = extractText(item)
        val paint = fonts.getBodyPaint()
        val markerWidth = paint.measureText("$marker ")
        
        val height = calculateTextHeight(text, paint)
        ensureSpace(height + LayoutUtils.LIST_ITEM_SPACING_PT, document)
        
        // Draw marker
        currentCanvas?.drawText(
            marker,
            pageSpec.marginSize.points + LayoutUtils.LIST_INDENT_PT,
            currentY + paint.textSize,
            paint
        )
        
        // Draw text
        drawTextWithOffset(text, paint, LayoutUtils.LIST_INDENT_PT + markerWidth)
        currentY += LayoutUtils.LIST_ITEM_SPACING_PT
    }
    
    private fun renderCodeBlock(code: Node, document: PdfDocument) {
        val text = when (code) {
            is FencedCodeBlock -> code.literal
            is IndentedCodeBlock -> code.literal
            else -> ""
        }
        
        val paint = fonts.getCodePaint()
        val bgPaint = fonts.getBackgroundPaint(0xFFF2F2F2.toInt())
        
        val lines = text.lines()
        val lineHeight = paint.textSize * 1.5f
        val padding = LayoutUtils.CODE_BLOCK_PADDING_PT
        
        // Calculate how many lines can fit on current page
        val availableHeight = pageSpec.effectiveHeight - pageSpec.marginSize.points - currentY
        val maxLinesOnCurrentPage = ((availableHeight - padding * 2) / lineHeight).toInt()
        
        if (lines.size <= maxLinesOnCurrentPage && maxLinesOnCurrentPage > 0) {
            // Code block fits on current page
            renderCodeBlockSegment(lines, paint, bgPaint, document)
        } else {
            // Split code block across pages
            var remainingLines = lines.toList()
            
            while (remainingLines.isNotEmpty()) {
                val availableHeight = pageSpec.effectiveHeight - pageSpec.marginSize.points - currentY
                val maxLines = if (currentY > pageSpec.marginSize.points + 50) {
                    // Not at top of page, check available space
                    ((availableHeight - padding * 2) / lineHeight).toInt()
                } else {
                    // At top of page, calculate max lines for full page
                    val fullPageHeight = pageSpec.effectiveHeight - (pageSpec.marginSize.points * 2)
                    ((fullPageHeight - padding * 2) / lineHeight).toInt()
                }
                
                if (maxLines <= 0) {
                    // Start new page
                    startNewPage(document)
                    continue
                }
                
                val linesToRender = remainingLines.take(maxLines)
                renderCodeBlockSegment(linesToRender, paint, bgPaint, document)
                
                remainingLines = remainingLines.drop(maxLines)
                
                // If there are more lines, start a new page
                if (remainingLines.isNotEmpty()) {
                    startNewPage(document)
                }
            }
        }
        
        currentY += LayoutUtils.PARAGRAPH_SPACING_PT
    }
    
    private fun renderCodeBlockSegment(lines: List<String>, paint: Paint, bgPaint: Paint, document: PdfDocument) {
        val lineHeight = paint.textSize * 1.5f
        val padding = LayoutUtils.CODE_BLOCK_PADDING_PT
        val segmentHeight = lines.size * lineHeight + (padding * 2)
        
        // Draw background
        val rect = RectF(
            pageSpec.marginSize.points,
            currentY,
            pageSpec.effectiveWidth - pageSpec.marginSize.points,
            currentY + segmentHeight
        )
        currentCanvas?.drawRoundRect(rect, LayoutUtils.CODE_BLOCK_RADIUS_PT, LayoutUtils.CODE_BLOCK_RADIUS_PT, bgPaint)
        
        // Draw code lines
        currentY += padding
        lines.forEach { line ->
            currentCanvas?.drawText(
                line,
                pageSpec.marginSize.points + padding,
                currentY + paint.textSize,
                paint
            )
            currentY += lineHeight
        }
        currentY += padding
    }
    
    private fun renderHorizontalRule(document: PdfDocument) {
        ensureSpace(20f, document)
        
        val paint = fonts.getLinePaint(Color.LTGRAY)
        currentY += 10f
        currentCanvas?.drawLine(
            pageSpec.marginSize.points,
            currentY,
            pageSpec.effectiveWidth - pageSpec.marginSize.points,
            currentY,
            paint
        )
        currentY += 10f
    }
    
    private fun drawText(text: String, paint: Paint) {
        val textPaint = TextPaint(paint)
        val width = (pageSpec.contentWidth).toInt()
        
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1.2f)
            .build()
        
        currentCanvas?.save()
        currentCanvas?.translate(pageSpec.marginSize.points, currentY)
        layout.draw(currentCanvas)
        currentCanvas?.restore()
        
        currentY += layout.height.toFloat()
    }
    
    private fun drawTextWithOffset(text: String, paint: Paint, offsetX: Float) {
        val textPaint = TextPaint(paint)
        val width = (pageSpec.contentWidth - offsetX).toInt()
        
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1.2f)
            .build()
        
        currentCanvas?.save()
        currentCanvas?.translate(pageSpec.marginSize.points + offsetX, currentY)
        layout.draw(currentCanvas)
        currentCanvas?.restore()
        
        currentY += layout.height.toFloat()
    }
    
    private fun calculateTextHeight(text: String, paint: Paint): Float {
        val textPaint = TextPaint(paint)
        val width = pageSpec.contentWidth.toInt()
        
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1.2f)
            .build()
        
        return layout.height.toFloat()
    }
    
    private fun ensureSpace(requiredHeight: Float, document: PdfDocument) {
        if (currentY + requiredHeight > pageSpec.effectiveHeight - pageSpec.marginSize.points) {
            startNewPage(document)
        }
    }
    
    private fun extractText(node: Node): String {
        val sb = StringBuilder()
        node.accept(object : AbstractVisitor() {
            override fun visit(text: Text) {
                sb.append(text.literal)
            }
            
            override fun visit(code: Code) {
                sb.append(code.literal)
            }
            
            override fun visit(emphasis: Emphasis) {
                visitChildren(emphasis)
            }
            
            override fun visit(strongEmphasis: StrongEmphasis) {
                visitChildren(strongEmphasis)
            }
            
            override fun visit(link: Link) {
                visitChildren(link)
            }
        })
        return sb.toString()
    }
}