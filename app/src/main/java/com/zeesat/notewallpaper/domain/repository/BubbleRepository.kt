package com.zeesat.notewallpaper.domain.repository

import com.zeesat.notewallpaper.domain.model.BubbleTemplate

interface BubbleRepository {
    fun getTemplates(): List<BubbleTemplate>
    fun getTemplateById(id: String): BubbleTemplate?
}
