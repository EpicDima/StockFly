package com.epicdima.stockfly.core.shortcuts

import com.epicdima.stockfly.core.model.Company

interface ShortcutsConfigurator {
    fun updateShortcuts(list: List<Company>)
}