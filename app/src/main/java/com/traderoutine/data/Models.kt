package com.traderoutine.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ModuleId {
    @SerialName("customer_reach")
    CUSTOMER_REACH,
    @SerialName("exposure_content")
    EXPOSURE_CONTENT,
    @SerialName("industry_observe")
    INDUSTRY_OBSERVE,
    @SerialName("account_maintenance")
    ACCOUNT_MAINTENANCE,
    @SerialName("product_tech")
    PRODUCT_TECH,
    @SerialName("wrap_up")
    WRAP_UP
}

@Serializable
data class ModuleOption(
    val id: String,
    val label: String
)

@Serializable
data class ModuleDefinition(
    val id: ModuleId,
    val title: String,
    val description: String,
    val options: List<ModuleOption> = emptyList(),
    val hasFreeText: Boolean = false,
    val statusOptions: List<String> = emptyList()
)

@Serializable
data class DayRecord(
    val dateIso: String,
    val completedModules: Set<ModuleId> = emptySet(),
    val selectedOptions: Map<ModuleId, Set<String>> = emptyMap(),
    val recapText: String? = null,
    val recapStatus: String? = null
)

@Serializable
data class AppSettings(
    val reminderEnabled: Boolean = false,
    val darkModeEnabled: Boolean = false,
    val weekendIncluded: Boolean = true
)
