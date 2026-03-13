package com.traderoutine.model

import java.util.Locale

enum class UiLanguage(val code: String, val locale: Locale) {
    ENGLISH("en", Locale.ENGLISH),
    CHINESE("zh", Locale.SIMPLIFIED_CHINESE);

    companion object {
        fun fromCode(code: String?): UiLanguage {
            return entries.firstOrNull { it.code == code } ?: ENGLISH
        }
    }
}
