package com.traderoutine.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.traderoutine.data.MoodStatus
import com.traderoutine.data.ModuleCatalog

@Composable
fun HomeScreen(viewModel: MainViewModel, contentPadding: PaddingValues) {
    val record by viewModel.todayRecord.collectAsState()
    val modules = ModuleCatalog.modules
    val completedCount = record.completedModulesCount()
    val isComplete = completedCount >= 3

    LazyColumn(
        contentPadding = PaddingValues(
            start = contentPadding.calculateStartPadding(LayoutDirection.Ltr) + 8.dp,
            end = contentPadding.calculateEndPadding(LayoutDirection.Ltr) + 8.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "今日打卡",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "完成 3 个模块即可打卡，不记分、不统计。",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isComplete) "今日已完成" else "今日未完成",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        modules.forEachIndexed { moduleIndex, module ->
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = module.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            if (module.optional) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "可选",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.outline,
                                )
                            }
                        }
                        module.description?.let { description ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        module.items.forEachIndexed { itemIndex, item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                            ) {
                                val checked = record.moduleItemStates
                                    .getOrNull(moduleIndex)
                                    ?.getOrNull(itemIndex) == true
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { viewModel.toggleItem(moduleIndex, itemIndex, it) },
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = item)
                            }
                        }

                        if (module.id == "wrap_up") {
                            MoodSelector(
                                selectedMood = record.moodStatus,
                                enabled = record.isModuleCompleted(moduleIndex),
                                onMoodSelected = viewModel::updateMood,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodSelector(
    selectedMood: MoodStatus?,
    enabled: Boolean,
    onMoodSelected: (MoodStatus?) -> Unit,
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(
            text = "状态",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.outline,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MoodStatus.entries.forEach { mood ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedMood == mood,
                        onClick = if (enabled) {
                            { onMoodSelected(mood) }
                        } else {
                            null
                        },
                        enabled = enabled,
                    )
                    Text(text = mood.label)
                }
            }
        }
        if (enabled) {
            Text(
                text = "不展开，只做情绪封口。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}
