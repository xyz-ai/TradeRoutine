package com.traderoutine.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traderoutine.data.AppSettings
import com.traderoutine.data.CheckInRepository
import com.traderoutine.data.DayRecord
import com.traderoutine.data.ModuleDefinition
import com.traderoutine.data.ModuleId
import com.traderoutine.data.ModuleOption
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class AppViewModel(private val repository: CheckInRepository) : ViewModel() {
    val settings: StateFlow<AppSettings> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    val records: StateFlow<List<DayRecord>> = repository.recordsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val todayRecord: StateFlow<DayRecord> = repository.recordForDate(LocalDate.now())
        .map { it ?: DayRecord(LocalDate.now().toString()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DayRecord(LocalDate.now().toString()))

    val modules: List<ModuleDefinition> = listOf(
        ModuleDefinition(
            id = ModuleId.CUSTOMER_REACH,
            title = "客户触达",
            description = "任意完成一项即可",
            options = listOf(
                ModuleOption("dm_email", "发客户私信 / 邮件"),
                ModuleOption("follow_old", "跟进老客户")
            )
        ),
        ModuleDefinition(
            id = ModuleId.EXPOSURE_CONTENT,
            title = "曝光 / 内容",
            description = "对外可见即可",
            options = listOf(
                ModuleOption("social_post", "发 Facebook / LinkedIn / 抖音 / TikTok 任意")
            )
        ),
        ModuleDefinition(
            id = ModuleId.INDUSTRY_OBSERVE,
            title = "行业 / 客户观察",
            description = "有目的地看",
            options = listOf(
                ModuleOption("watch_peer", "看同行"),
                ModuleOption("watch_customer", "看客户动态"),
                ModuleOption("watch_industry", "看行业内容")
            )
        ),
        ModuleDefinition(
            id = ModuleId.ACCOUNT_MAINTENANCE,
            title = "账号日程活跃维护",
            description = "模拟正常使用行为",
            options = listOf(
                ModuleOption("account_active", "Facebook / 小红书 / 抖音 / TikTok 账号活跃维护")
            )
        ),
        ModuleDefinition(
            id = ModuleId.PRODUCT_TECH,
            title = "产品 / 技术",
            description = "哪怕 5 分钟也算",
            options = listOf(
                ModuleOption("review_drawing", "看图纸 / 模具"),
                ModuleOption("learn_process", "学一点工艺 / 产品")
            )
        ),
        ModuleDefinition(
            id = ModuleId.WRAP_UP,
            title = "收尾",
            description = "一句话复盘",
            hasFreeText = true,
            statusOptions = listOf("顺", "一般", "累", "烦")
        )
    )

    fun toggleOption(record: DayRecord, moduleId: ModuleId, optionId: String) {
        val selected = record.selectedOptions[moduleId].orEmpty().toMutableSet()
        if (selected.contains(optionId)) {
            selected.remove(optionId)
        } else {
            selected.add(optionId)
        }
        val updatedSelected = record.selectedOptions.toMutableMap().apply {
            this[moduleId] = selected
        }
        val completedModules = record.completedModules.toMutableSet().apply {
            if (selected.isNotEmpty()) add(moduleId) else remove(moduleId)
        }
        saveRecord(record.copy(selectedOptions = updatedSelected, completedModules = completedModules))
    }

    fun updateRecap(record: DayRecord, text: String) {
        val normalized = text.ifBlank { null }
        val completedModules = record.completedModules.toMutableSet().apply {
            if (normalized != null || !record.recapStatus.isNullOrBlank()) add(ModuleId.WRAP_UP) else remove(ModuleId.WRAP_UP)
        }
        saveRecord(record.copy(recapText = normalized, completedModules = completedModules))
    }

    fun updateRecapStatus(record: DayRecord, status: String) {
        val normalized = status.ifBlank { null }
        val completedModules = record.completedModules.toMutableSet().apply {
            if (normalized != null || !record.recapText.isNullOrBlank()) add(ModuleId.WRAP_UP) else remove(ModuleId.WRAP_UP)
        }
        saveRecord(record.copy(recapStatus = normalized, completedModules = completedModules))
    }

    fun updateSettings(update: (AppSettings) -> AppSettings) {
        viewModelScope.launch {
            repository.updateSettings(update)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    private fun saveRecord(record: DayRecord) {
        viewModelScope.launch {
            repository.updateRecord(record)
        }
    }
}
