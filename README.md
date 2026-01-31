# TradeRoutine V1 - 外贸日常打卡（极简版）

## 项目结构说明
```
TradeRoutine/
├─ app/
│  ├─ src/main/
│  │  ├─ AndroidManifest.xml
│  │  ├─ java/com/traderoutine/
│  │  │  ├─ MainActivity.kt
│  │  │  ├─ data/
│  │  │  │  ├─ Models.kt
│  │  │  │  └─ TradeRoutineStore.kt
│  │  │  └─ ui/
│  │  │     ├─ AppRoot.kt
│  │  │     ├─ HomeScreen.kt
│  │  │     ├─ RecordsScreen.kt
│  │  │     ├─ SettingsScreen.kt
│  │  │     └─ Theme.kt
│  │  └─ res/
│  │     ├─ drawable/ (底部导航图标)
│  │     └─ values/themes.xml
│  └─ build.gradle.kts
├─ build.gradle.kts
└─ settings.gradle.kts
```

## 主要数据结构（Module、DayRecord 等）
- `ModuleId` / `TaskId`：定义模块与任务的枚举。
- `ModuleDefinition` / `TaskDefinition`：模块与任务的静态结构描述，用于 UI 固定渲染。
- `DayRecord`：单日记录（日期、已勾选任务、复盘文字与情绪）。
- `SettingsState`：设置项状态。
- `AppState`：应用整体状态（记录 + 设置）。

详见 `Models.kt`。

## 核心 Compose 页面代码
- `HomeScreen.kt`：今日打卡页面，固定模块与任务，完成 ≥3 模块即打卡成功。
- `RecordsScreen.kt`：日历视图 + 连续打卡 + 最近 7 天概览。
- `SettingsScreen.kt`：提醒、深色模式、周末计入、清空本地数据。
- `AppRoot.kt`：底部导航与页面路由。

## 本地存储逻辑示例
- 使用 **DataStore Preferences**。
- 记录数据 `records_json` 以 JSON 形式持久化（`DayRecord` 的 Map）。
- 设置项存储为独立 key。
- 在 `TradeRoutineStore` 中集中更新记录与设置。

## V2 扩展方向（不实现）
1. **多设备同步**：引入本地数据库 + 云同步（可选开关，不强制登录）。
2. **自定义提醒规则**：按工作日/节假日提醒、柔性提醒时间段。
3. **统计维度优化**：保持不计数量/质量的前提下，仅提供周/月趋势色块，不展示分数。
4. **轻量模板**：允许用户为复盘句子设置“简短模板”，减少输入负担。
5. **数据导出**：仅导出 CSV / JSON，方便个人备份。
