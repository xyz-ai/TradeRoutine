package com.traderoutine.data

import kotlinx.serialization.Serializable

@Serializable
enum class ModuleId {
    CUSTOMER_REACH,
    EXPOSURE_CONTENT,
    MARKET_OBSERVE,
    ACCOUNT_MAINTAIN,
    PRODUCT_TECH,
    WRAP_UP
}

@Serializable
enum class TaskId {
    MESSAGE_CUSTOMER,
    FOLLOW_UP,
    POST_SOCIAL,
    WATCH_COMPETITOR,
    WATCH_CUSTOMER,
    WATCH_INDUSTRY,
    MAINTAIN_ACCOUNT,
    REVIEW_DRAWING,
    LEARN_PROCESS,
    WRAP_UP_NOTE
}

@Serializable
enum class Mood {
    SMOOTH,
    NORMAL,
    TIRED,
    ANNOYED
}

@Serializable
data class DayRecord(
    val date: String,
    val checkedTasks: Set<TaskId> = emptySet(),
    val wrapUpNote: String = "",
    val mood: Mood? = null
)

@Serializable
data class SettingsState(
    val reminderEnabled: Boolean = true,
    val darkMode: Boolean = false,
    val weekendIncluded: Boolean = true
)

@Serializable
data class AppState(
    val records: Map<String, DayRecord> = emptyMap(),
    val settings: SettingsState = SettingsState()
)

data class ModuleDefinition(
    val id: ModuleId,
    val title: String,
    val tasks: List<TaskDefinition>
)

data class TaskDefinition(
    val id: TaskId,
    val label: String
)

object ModulesCatalog {
    val modules = listOf(
        ModuleDefinition(
            id = ModuleId.CUSTOMER_REACH,
            title = "客户触达",
            tasks = listOf(
                TaskDefinition(TaskId.MESSAGE_CUSTOMER, "发客户私信 / 邮件"),
                TaskDefinition(TaskId.FOLLOW_UP, "跟进老客户")
            )
        ),
        ModuleDefinition(
            id = ModuleId.EXPOSURE_CONTENT,
            title = "曝光 / 内容",
            tasks = listOf(
                TaskDefinition(TaskId.POST_SOCIAL, "发 Facebook / LinkedIn / 抖音 / TikTok 任意")
            )
        ),
        ModuleDefinition(
            id = ModuleId.MARKET_OBSERVE,
            title = "行业 / 客户观察",
            tasks = listOf(
                TaskDefinition(TaskId.WATCH_COMPETITOR, "看同行"),
                TaskDefinition(TaskId.WATCH_CUSTOMER, "看客户动态"),
                TaskDefinition(TaskId.WATCH_INDUSTRY, "看行业内容")
            )
        ),
        ModuleDefinition(
            id = ModuleId.ACCOUNT_MAINTAIN,
            title = "账号日程活跃维护",
            tasks = listOf(
                TaskDefinition(TaskId.MAINTAIN_ACCOUNT, "Facebook / 小红书 / 抖音 / TikTok 账号活跃维护")
            )
        ),
        ModuleDefinition(
            id = ModuleId.PRODUCT_TECH,
            title = "产品 / 技术",
            tasks = listOf(
                TaskDefinition(TaskId.REVIEW_DRAWING, "看图纸 / 模具"),
                TaskDefinition(TaskId.LEARN_PROCESS, "学一点工艺 / 产品")
            )
        ),
        ModuleDefinition(
            id = ModuleId.WRAP_UP,
            title = "收尾（可选但强烈推荐）",
            tasks = listOf(
                TaskDefinition(TaskId.WRAP_UP_NOTE, "一句话复盘")
            )
        )
    )
}
