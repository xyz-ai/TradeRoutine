# TradeRoutine V1（极简外贸日常打卡）

> 定位：确认“今天是否完成了外贸应做的基本行为”，不做质量评估、不统计数量、不制造压力。

## 项目结构说明

```
TradeRoutine/
├─ app/
│  ├─ src/main/
│  │  ├─ AndroidManifest.xml
│  │  ├─ java/com/traderoutine/
│  │  │  ├─ MainActivity.kt
│  │  │  ├─ data/
│  │  │  │  ├─ CheckInRepository.kt
│  │  │  │  ├─ CheckInStore.kt
│  │  │  │  └─ Models.kt
│  │  │  ├─ ui/
│  │  │  │  ├─ App.kt
│  │  │  │  ├─ AppViewModel.kt
│  │  │  │  ├─ AppViewModelFactory.kt
│  │  │  │  └─ screens/
│  │  │  │     ├─ HomeScreen.kt
│  │  │  │     ├─ RecordsScreen.kt
│  │  │  │     └─ SettingsScreen.kt
│  │  │  └─ ui/theme/
│  │  │     ├─ Theme.kt
│  │  │     └─ Typography.kt
│  │  └─ res/
│  │     ├─ drawable/
│  │     └─ values/
│  └─ build.gradle.kts
├─ build.gradle.kts
└─ settings.gradle.kts
```

## 主要数据结构

- **ModuleDefinition**：固定模块定义（标题、说明、选项）。
- **DayRecord**：某日完成状态（完成模块集合、选项选择、复盘文本/情绪）。
- **AppSettings**：提醒/深色模式/周末是否计入。

详见：`app/src/main/java/com/traderoutine/data/Models.kt`。

## 核心 Compose 页面

- **首页（今日打卡）**：固定 6 模块，任意完成模块内任意选项即完成该模块；完成 ≥3 视为今日完成。`HomeScreen.kt`
- **记录页**：月历 + 近 7 天简表 + 连续打卡天数。`RecordsScreen.kt`
- **设置页**：仅保留 4 项设置。`SettingsScreen.kt`

## 本地存储逻辑示例（DataStore）

- 使用 **DataStore Preferences** 保存 `AppSettings` 与 `DayRecord` 列表。
- 通过 `kotlinx.serialization` JSON 序列化存储。

核心实现在 `CheckInStore.kt` 与 `CheckInRepository.kt`。

## V2 扩展方向（不实现）

- **统计维度**：按周/按月趋势仅展示“完成/未完成”折线或条形图（仍不涉及数量）。
- **提醒优化**：支持自定义提醒时间（单次、工作日），仍保持本地化。
- **多模板**：提供少量“行业模板”（例如机械、纺织），但仍不允许用户自由增删模块。
- **导出**：导出为本地图片或 CSV（只导出完成标记，不导出内容细节）。
