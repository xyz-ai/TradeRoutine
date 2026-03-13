package com.traderoutine.ui.navigation

object Routes {
    const val TASKS = "tasks"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"
    const val TASK_EDITOR = "task_editor"
    const val DAY_DETAIL = "day_detail"
    const val ABOUT = "about"
    const val DISCLAIMER = "disclaimer"

    const val TEMPLATE_ID = "templateId"
    const val DATE = "date"

    fun taskEditor(templateId: Long? = null): String {
        return if (templateId == null) {
            TASK_EDITOR
        } else {
            "$TASK_EDITOR?$TEMPLATE_ID=$templateId"
        }
    }

    fun dayDetail(date: String): String = "$DAY_DETAIL/$date"
}
