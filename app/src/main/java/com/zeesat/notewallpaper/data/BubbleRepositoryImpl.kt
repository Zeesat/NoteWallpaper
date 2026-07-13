package com.zeesat.notewallpaper.data

import com.zeesat.notewallpaper.domain.model.BubbleTemplate
import com.zeesat.notewallpaper.domain.repository.BubbleRepository

class BubbleRepositoryImpl : BubbleRepository {
    private val templates = listOf(
        BubbleTemplate("classic", "Classic Bubble", "bubbles/classic.png"),
        BubbleTemplate("modern", "Modern Bubble", "bubbles/modern.png"),
        BubbleTemplate("sticky", "Sticky Note", "bubbles/sticky.png")
    )

    override fun getTemplates(): List<BubbleTemplate> = templates

    override fun getTemplateById(id: String): BubbleTemplate? {
        return templates.find { it.id == id }
    }
}
