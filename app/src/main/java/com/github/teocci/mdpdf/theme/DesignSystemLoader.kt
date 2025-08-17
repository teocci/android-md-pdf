package com.github.teocci.mdpdf.theme

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import org.json.JSONObject
import java.io.IOException

data class DesignSystem(
    val colors: DesignColors,
    val typography: DesignTypography,
    val spacing: DesignSpacing,
    val radius: DesignRadius
)

data class DesignColors(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val outline: Color
)

data class DesignTypography(
    val titleFont: String,
    val bodyFont: String,
    val monoFont: String
)

data class DesignSpacing(
    val xs: Int,
    val sm: Int,
    val md: Int,
    val lg: Int,
    val xl: Int
)

data class DesignRadius(
    val sm: Int,
    val md: Int,
    val lg: Int
)

object DesignSystemLoader {
    private const val TAG = "DesignSystemLoader"
    private const val DESIGN_SYSTEM_FILE = "design_system.json"
    
    fun load(context: Context): DesignSystem {
        return try {
            val jsonString = context.assets.open(DESIGN_SYSTEM_FILE).bufferedReader().use { it.readText() }
            parseDesignSystem(jsonString)
        } catch (e: IOException) {
            Log.w(TAG, "Failed to load design system, using defaults", e)
            getDefaultDesignSystem()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse design system, using defaults", e)
            getDefaultDesignSystem()
        }
    }
    
    private fun parseDesignSystem(jsonString: String): DesignSystem {
        val json = JSONObject(jsonString)
        
        val colorsJson = json.getJSONObject("colors")
        val colors = DesignColors(
            primary = parseColor(colorsJson.getString("primary")),
            onPrimary = parseColor(colorsJson.getString("onPrimary")),
            secondary = parseColor(colorsJson.getString("secondary")),
            onSecondary = parseColor(colorsJson.getString("onSecondary")),
            surface = parseColor(colorsJson.getString("surface")),
            onSurface = parseColor(colorsJson.getString("onSurface")),
            surfaceVariant = parseColor(colorsJson.getString("surfaceVariant")),
            outline = parseColor(colorsJson.getString("outline"))
        )
        
        val typographyJson = json.getJSONObject("typography")
        val typography = DesignTypography(
            titleFont = typographyJson.getString("titleFont"),
            bodyFont = typographyJson.getString("bodyFont"),
            monoFont = typographyJson.getString("monoFont")
        )
        
        val spacingJson = json.getJSONObject("spacing")
        val spacing = DesignSpacing(
            xs = spacingJson.getInt("xs"),
            sm = spacingJson.getInt("sm"),
            md = spacingJson.getInt("md"),
            lg = spacingJson.getInt("lg"),
            xl = spacingJson.getInt("xl")
        )
        
        val radiusJson = json.getJSONObject("radius")
        val radius = DesignRadius(
            sm = radiusJson.getInt("sm"),
            md = radiusJson.getInt("md"),
            lg = radiusJson.getInt("lg")
        )
        
        return DesignSystem(colors, typography, spacing, radius)
    }
    
    private fun parseColor(hexString: String): Color {
        val hex = hexString.removePrefix("#")
        val colorInt = hex.toLong(16)
        return Color(0xFF000000 or colorInt)
    }
    
    private fun getDefaultDesignSystem(): DesignSystem {
        return DesignSystem(
            colors = DesignColors(
                primary = Color(0xFFF4A261),
                onPrimary = Color(0xFF1B1B1B),
                secondary = Color(0xFF4D4D4D),
                onSecondary = Color(0xFFFFFFFF),
                surface = Color(0xFFFFFFFF),
                onSurface = Color(0xFF1B1B1B),
                surfaceVariant = Color(0xFFF2F2F2),
                outline = Color(0xFFD0D0D0)
            ),
            typography = DesignTypography(
                titleFont = "Montserrat",
                bodyFont = "Roboto",
                monoFont = "JetBrainsMono"
            ),
            spacing = DesignSpacing(
                xs = 4,
                sm = 8,
                md = 16,
                lg = 24,
                xl = 32
            ),
            radius = DesignRadius(
                sm = 8,
                md = 12,
                lg = 16
            )
        )
    }
}