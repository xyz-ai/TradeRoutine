# TradeRoutine V1（极简外贸日常打卡）

本项目为极简 Android 日常执行型 App（V1），目标是确认“今天是否完成了外贸应做的基本行为”，不评估质量、不统计数量、不制造压力。

## 项目结构说明

```
TradeRoutine/
├─ app/
│  ├─ src/main/java/com/traderoutine/
│  │  ├─ MainActivity.kt              # 入口 Activity
│  │  ├─ data/
│  │  │  ├─ Models.kt                  # 模块定义、数据模型
│  │  │  └─ LocalStore.kt              # Room 数据库、DAO、Repository
│  │  └─ ui/
│  │     ├─ MainViewModel.kt           # 业务状态与交互逻辑
│  │     ├─ TradeRoutineApp.kt         # 导航与底部栏
│  │     ├─ HomeScreen.kt              # 今日打卡
│  │     ├─ RecordsScreen.kt           # 打卡记录
│  │     ├─ SettingsScreen.kt          # 设置页
│  │     └─ theme/Theme.kt             # 主题
│  └─ src/main/res/                    # 资源文件
├─ build.gradle.kts
└─ settings.gradle.kts
```

## 主要数据结构（Module / DayRecord）

- **ModuleDefinition**：固定模块定义，包含模块标题、子项列表、可选标记。
- **DayRecord**：每天的打卡数据，包含日期、每个模块的子项勾选、情绪状态。
- **MoodStatus**：顺 / 一般 / 累 / 烦。用于收尾模块的情绪封口。

关键数据结构片段（简化）：

```kotlin
data class ModuleDefinition(
    val id: String,
    val title: String,
    val items: List<String>,
    val optional: Boolean = false,
)

data class DayRecord(
    val date: LocalDate,
    val moduleItemStates: List<List<Boolean>>,
    val moodStatus: MoodStatus?,
)
```

## 核心 Compose 页面代码

- **首页：今日打卡**：固定 6 个模块（不可自定义），任意完成一个子项即可视为完成该模块。
- **完成规则**：完成 ≥3 个模块即今日打卡成功。

关键片段（节选）：

```kotlin
val completedCount = record.completedModulesCount()
val isComplete = completedCount >= 3
Text(text = if (isComplete) "今日已完成" else "今日未完成")
```

## 本地存储逻辑示例（Room）

- 使用 Room 保存 `DayRecordEntity` 与 `SettingsEntity`。
- 模块子项勾选状态被编码成字符串存储，读取后解码成列表。

```kotlin
private fun encodeRecord(record: DayRecord): DayRecordEntity {
    val encodedModules = record.moduleItemStates.joinToString("|") { moduleItems ->
        moduleItems.joinToString("") { if (it) "1" else "0" }
    }
    return DayRecordEntity(
        date = record.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
        moduleItemsEncoded = encodedModules,
        mood = record.moodStatus?.name,
    )
}
```

## 如何扩展到 V2（仅说明，不实现）

- **提醒策略**：增加时间段配置（如早/晚），但仍保持“开/关”简化入口。
- **模块自定义**：允许用户微调顺序或隐藏模块（不改变“固定 6 模块”的理念）。
- **轻量统计**：仅显示月度完成天数，不提供评分与排名。
- **多端同步**：增加本地导出/导入功能（避免强依赖账号体系）。

---

> 运行：使用 Android Studio 打开并运行 `app` 模块即可。
