package com.zeesat.notewallpaper.domain.model

data class FontOption(
    val id: String,
    val displayName: String,
    val googleFontName: String? = null,
    val category: String = "Built-in"
) {
    val isBuiltIn: Boolean get() = category == "Built-in"
}
