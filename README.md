# TradeRoutine V1 – 极简 Android 外贸日常打卡 App

> 目标：确认“今天是否完成了外贸应做的基本行为”，只记录做 / 没做，不评估质量、不统计数量、不制造压力。

## 1. 项目结构说明（建议）
```
app/
  src/main/java/com/traderoutine/
    data/
      model/
        Module.kt
        DayRecord.kt
        Settings.kt
      local/
        AppPreferences.kt      // DataStore
        DayRecordDao.kt        // Room 方案可选
    domain/
      CheckinPolicy.kt
      CheckinRepository.kt
    ui/
      theme/
      HomeScreen.kt
      RecordScreen.kt
      SettingsScreen.kt
    MainActivity.kt
```

> V1 可优先使用 **DataStore**（简洁、无表结构），需要日历查询时再迁移 Room。

---

## 2. 主要数据结构

### 2.1 Module（固定模块定义）
```kotlin
enum class ModuleId {
    CUSTOMER_OUTREACH,
    EXPOSURE_CONTENT,
    INDUSTRY_OBSERVE,
    ACCOUNT_ACTIVITY,
    PRODUCT_TECH,
    WRAP_UP
}

data class Module(
    val id: ModuleId,
    val title: String,
    val items: List<String>
)

val FixedModules = listOf(
    Module(
        id = ModuleId.CUSTOMER_OUTREACH,
        title = "客户触达",
        items = listOf("发客户私信 / 邮件", "跟进老客户")
    ),
    Module(
        id = ModuleId.EXPOSURE_CONTENT,
        title = "曝光 / 内容",
        items = listOf("发 Facebook / LinkedIn / 抖音 / TikTok 任意")
    ),
    Module(
        id = ModuleId.INDUSTRY_OBSERVE,
        title = "行业 / 客户观察",
        items = listOf("看同行", "看客户动态", "看行业内容")
    ),
    Module(
        id = ModuleId.ACCOUNT_ACTIVITY,
        title = "账号日程活跃维护",
        items = listOf("Facebook / 小红书 / 抖音 / TikTok 账号活跃维护")
    ),
    Module(
        id = ModuleId.PRODUCT_TECH,
        title = "产品 / 技术",
        items = listOf("看图纸 / 模具", "学一点工艺 / 产品")
    ),
    Module(
        id = ModuleId.WRAP_UP,
        title = "收尾（可选但推荐）",
        items = listOf("一句话复盘")
    )
)
```

### 2.2 DayRecord（每日打卡状态）
```kotlin
import java.time.LocalDate

enum class WrapUpMood { SMOOTH, NORMAL, TIRED, ANNOYED }

/**
 * 仅保存“做 / 没做”。不存行为质量、不存数量。
 */
data class DayRecord(
    val date: LocalDate,
    val doneModules: Set<ModuleId>,
    val wrapUpMood: WrapUpMood? = null,
    val wrapUpNote: String? = null
) {
    val completedCount: Int get() = doneModules.size
    val isSuccess: Boolean get() = completedCount >= 3
}
```

### 2.3 Settings（设置项）
```kotlin
data class Settings(
    val reminderEnabled: Boolean = false,
    val darkModeEnabled: Boolean = false,
    val includeWeekend: Boolean = true
)
```

---

## 3. 核心 Compose 页面代码（示例）

### 3.1 首页 – 今日打卡
```kotlin
@Composable
fun HomeScreen(
    today: DayRecord,
    onToggleModule: (ModuleId) -> Unit,
    onSelectMood: (WrapUpMood) -> Unit,
    onWrapUpNoteChange: (String) -> Unit
) {
    val completed = today.completedCount
    val success = today.isSuccess

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "今日打卡", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        FixedModules.forEach { module ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = today.doneModules.contains(module.id),
                            onCheckedChange = { onToggleModule(module.id) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = module.title, style = MaterialTheme.typography.titleMedium)
                    }
                    module.items.forEach { item ->
                        Text(text = "• $item", style = MaterialTheme.typography.bodyMedium)
                    }

                    if (module.id == ModuleId.WRAP_UP) {
                        Spacer(Modifier.height(8.dp))
                        Row {
                            listOf(
                                WrapUpMood.SMOOTH to "顺",
                                WrapUpMood.NORMAL to "一般",
                                WrapUpMood.TIRED to "累",
                                WrapUpMood.ANNOYED to "烦"
                            ).forEach { (mood, label) ->
                                FilterChip(
                                    selected = today.wrapUpMood == mood,
                                    onClick = { onSelectMood(mood) },
                                    label = { Text(label) }
                                )
                                Spacer(Modifier.width(6.dp))
                            }
                        }
                        OutlinedTextField(
                            value = today.wrapUpNote.orEmpty(),
                            onValueChange = onWrapUpNoteChange,
                            placeholder = { Text("一句话复盘") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
        Text(
            text = if (success) "今日打卡成功" else "完成 3 个模块即可打卡",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(text = "已完成：$completed / 6", style = MaterialTheme.typography.bodyMedium)
    }
}
```

### 3.2 记录页 – 日历 + 连续天数 + 最近 7 天
```kotlin
@Composable
fun RecordScreen(
    calendarDays: List<DayRecord>,
    streakDays: Int,
    recent7: List<DayRecord>
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("打卡记录", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        Text("连续打卡：$streakDays 天", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))

        // 简化示意：日历栅格
        LazyVerticalGrid(columns = GridCells.Fixed(7)) {
            items(calendarDays) { day ->
                val color = when {
                    day.completedCount >= 3 -> Color(0xFF66BB6A)
                    day.completedCount in 1..2 -> Color(0xFFFFD54F)
                    else -> Color(0xFFBDBDBD)
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .padding(2.dp)
                        .background(color, shape = CircleShape)
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("最近 7 天", style = MaterialTheme.typography.titleMedium)
        recent7.forEach { day ->
            Text(text = "${day.date}：完成 ${day.completedCount} 模块")
        }
    }
}
```

### 3.3 设置页 – 极简开关
```kotlin
@Composable
fun SettingsScreen(
    settings: Settings,
    onToggleReminder: (Boolean) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleWeekend: (Boolean) -> Unit,
    onClearData: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("设置", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        SwitchRow("打卡提醒", settings.reminderEnabled, onToggleReminder)
        SwitchRow("深色模式", settings.darkModeEnabled, onToggleDarkMode)
        SwitchRow("周末计入", settings.includeWeekend, onToggleWeekend)

        Spacer(Modifier.height(20.dp))
        Button(onClick = onClearData, colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFEF5350)
        )) {
            Text("清空本地数据")
        }
    }
}

@Composable
private fun SwitchRow(title: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}
```

---

## 4. 本地存储逻辑示例（DataStore）

```kotlin
object AppPreferences {
    private val Context.dataStore by preferencesDataStore(name = "traderoutine")

    private val KEY_SETTINGS = stringPreferencesKey("settings")
    private val KEY_DAY_RECORDS = stringPreferencesKey("day_records")

    suspend fun saveSettings(context: Context, settings: Settings) {
        val json = Json.encodeToString(settings)
        context.dataStore.edit { it[KEY_SETTINGS] = json }
    }

    fun settingsFlow(context: Context): Flow<Settings> =
        context.dataStore.data.map {
            it[KEY_SETTINGS]?.let { json -> Json.decodeFromString(json) } ?: Settings()
        }

    suspend fun saveDayRecords(context: Context, records: List<DayRecord>) {
        val json = Json.encodeToString(records)
        context.dataStore.edit { it[KEY_DAY_RECORDS] = json }
    }

    fun dayRecordsFlow(context: Context): Flow<List<DayRecord>> =
        context.dataStore.data.map {
            it[KEY_DAY_RECORDS]?.let { json -> Json.decodeFromString(json) } ?: emptyList()
        }
}
```

---

## 5. V2 可能扩展（不实现）

1. **多端同步**（仅当用户需要）：引入本地加密 + 可选云端备份。
2. **更清晰的周视图**：带周末开关逻辑、按周分区展示。
3. **提醒优化**：静默提醒、可选下班后提醒。
4. **轻量统计趋势**：只统计“做/没做”的连续性，不显示排名和分数。
5. **多语言支持**：为海外团队使用准备。

---

## 6. 产品风格说明
- **克制**：不添加激励文案、不制造紧张感。
- **简单**：只记录“做/没做”，不追踪细节。
- **允许下班**：完成 3 项即可结束今天。

