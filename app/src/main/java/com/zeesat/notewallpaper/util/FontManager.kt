package com.zeesat.notewallpaper.util

import android.content.Context
import android.graphics.Typeface
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.core.content.res.ResourcesCompat
import com.zeesat.notewallpaper.R
import com.zeesat.notewallpaper.domain.model.FontOption

object FontManager {
    val fontOptions: List<FontOption> = listOf(
        FontOption("sans-serif", "Sans Serif", category = "Built-in"),
        FontOption("serif", "Serif", category = "Built-in"),
        FontOption("monospace", "Monospace", category = "Built-in"),
        FontOption("casual", "Casual", category = "Built-in"),
        FontOption("cursive", "Cursive", category = "Built-in"),
        FontOption("cherry-bomb-one", "Cherry Bomb One", category = "External"),
        FontOption("caveat", "Caveat", category = "External"),
        FontOption("press-start-2p", "Press Start 2P", category = "External"),
        FontOption("bebas-neue", "Bebas Neue", category = "External")
    )

    private val fontFamilyCache = mutableMapOf<String, FontFamily>()

    fun getFontFamily(fontOption: FontOption): FontFamily {
        if (fontOption.isBuiltIn) {
            return when (fontOption.id) {
                "serif" -> androidx.compose.ui.text.font.FontFamily.Serif
                "monospace" -> androidx.compose.ui.text.font.FontFamily.Monospace
                "casual" -> androidx.compose.ui.text.font.FontFamily.Cursive
                "cursive" -> androidx.compose.ui.text.font.FontFamily.Cursive
                else -> androidx.compose.ui.text.font.FontFamily.SansSerif
            }
        }

        fontFamilyCache[fontOption.id]?.let { return it }

        val family = externalFontRes(fontOption.id)?.let { FontFamily(Font(it)) }
            ?: FontFamily.SansSerif
        fontFamilyCache[fontOption.id] = family
        return family
    }

    fun resolveTypeface(context: Context, fontOption: FontOption): Typeface {
        return if (fontOption.isBuiltIn) {
            when (fontOption.id) {
                "serif" -> Typeface.SERIF
                "monospace" -> Typeface.MONOSPACE
                "casual" -> Typeface.create("casual", Typeface.NORMAL)
                "cursive" -> Typeface.create("cursive", Typeface.NORMAL)
                else -> Typeface.SANS_SERIF
            }
        } else {
            externalFontRes(fontOption.id)?.let { ResourcesCompat.getFont(context, it) }
                ?: Typeface.SANS_SERIF
        }
    }

    fun getOptionById(id: String): FontOption {
        return fontOptions.firstOrNull { it.id == id } ?: fontOptions.first()
    }

    private fun externalFontRes(id: String): Int? {
        return when (id) {
            "cherry-bomb-one" -> R.font.cherry_bomb_one_regular
            "caveat" -> R.font.caveat_regular
            "press-start-2p" -> R.font.press_start_2p_regular
            "bebas-neue" -> R.font.bebas_neue_regular
            else -> null
        }
    }
}
