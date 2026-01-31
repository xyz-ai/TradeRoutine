package com.traderoutine.data

import java.time.LocalDate

enum class MoodStatus(val label: String) {
    SMOOTH("顺"),
    NORMAL("一般"),
    TIRED("累"),
    ANNOYED("烦"),
}

data class ModuleDefinition(
    val id: String,
    val title: String,
    val description: String? = null,
    val items: List<String>,
    val optional: Boolean = false,
)

data class DayRecord(
    val date: LocalDate,
    val moduleItemStates: List<List<Boolean>>,
    val moodStatus: MoodStatus?,
) {
    fun completedModulesCount(): Int = moduleItemStates.count { it.any { checked -> checked } }

    fun isModuleCompleted(index: Int): Boolean = moduleItemStates.getOrNull(index)?.any { it } == true
}

object ModuleCatalog {
    val modules: List<ModuleDefinition> = listOf(
        ModuleDefinition(
            id = "customer_touch",
            title = "客户触达",
            items = listOf("发客户私信 / 邮件", "跟进老客户"),
        ),
        ModuleDefinition(
            id = "exposure_content",
            title = "曝光 / 内容",
            items = listOf("发 Facebook / LinkedIn / 抖音 / TikTok 任意"),
        ),
        ModuleDefinition(
            id = "observation",
            title = "行业 / 客户观察",
            items = listOf("看同行", "看客户动态", "看行业内容"),
            description = "有目的地看，不是刷",
        ),
        ModuleDefinition(
            id = "account_activity",
            title = "账号日程活跃维护",
            items = listOf("Facebook / 小红书 / 抖音 / TikTok 账号活跃维护"),
        ),
        ModuleDefinition(
            id = "product_tech",
            title = "产品 / 技术",
            items = listOf("看图纸 / 模具", "学一点工艺 / 产品"),
        ),
        ModuleDefinition(
            id = "wrap_up",
            title = "收尾",
            items = listOf("一句话复盘"),
            optional = true,
        ),
    )

    fun defaultModuleStates(): List<List<Boolean>> = modules.map { module ->
        List(module.items.size) { false }
    }
}
