package com.github.teocci.mdpdf.pdf

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.github.teocci.mdpdf.R

class Fonts(context: Context) {
    private val montserratRegular: Typeface? = ResourcesCompat.getFont(context, R.font.montserrat_regular)
    private val montserratBold: Typeface? = ResourcesCompat.getFont(context, R.font.montserrat_bold)
    private val robotoRegular: Typeface? = ResourcesCompat.getFont(context, R.font.roboto_regular)
    private val robotoBold: Typeface? = ResourcesCompat.getFont(context, R.font.roboto_bold)
    private val jetbrainsMono: Typeface? = ResourcesCompat.getFont(context, R.font.jetbrains_mono_regular)
    
    fun getTitlePaint(sizePt: Float, bold: Boolean = true): Paint {
        return Paint().apply {
            isAntiAlias = true
            textSize = LayoutUtils.ptToPx(sizePt)
            typeface = if (bold) montserratBold ?: Typeface.DEFAULT_BOLD else montserratRegular ?: Typeface.DEFAULT
        }
    }
    
    fun getBodyPaint(sizePt: Float = LayoutUtils.BODY_SIZE_PT, bold: Boolean = false): Paint {
        return Paint().apply {
            isAntiAlias = true
            textSize = LayoutUtils.ptToPx(sizePt)
            typeface = if (bold) robotoBold ?: Typeface.DEFAULT_BOLD else robotoRegular ?: Typeface.DEFAULT
        }
    }
    
    fun getCodePaint(sizePt: Float = LayoutUtils.CODE_SIZE_PT): Paint {
        return Paint().apply {
            isAntiAlias = true
            textSize = LayoutUtils.ptToPx(sizePt)
            typeface = jetbrainsMono ?: Typeface.MONOSPACE
        }
    }
    
    fun getBackgroundPaint(color: Int): Paint {
        return Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            this.color = color
        }
    }
    
    fun getLinePaint(color: Int, strokeWidth: Float = 1f): Paint {
        return Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            this.color = color
            this.strokeWidth = strokeWidth
        }
    }
}