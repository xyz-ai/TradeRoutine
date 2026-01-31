package com.traderoutine.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.traderoutine.data.AppState
import com.traderoutine.data.ModuleId
import com.traderoutine.data.Mood
import com.traderoutine.data.ModulesCatalog
import com.traderoutine.data.TradeRoutineStore
import java.time.LocalDate
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(store: TradeRoutineStore, appState: AppState) {
    val today = LocalDate.now()
    val record = appState.records[today.toString()]
    val checked = record?.checkedTasks.orEmpty()
    val wrapUpNote = record?.wrapUpNote.orEmpty()
    val mood = record?.mood
    val scope = rememberCoroutineScope()

    val completedModules = ModulesCatalog.modules.count { module ->
        module.tasks.any { checked.contains(it.id) }
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "今日打卡",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "完成 ≥3 个模块即可打卡成功，不计数量、不评质量。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "已完成模块：$completedModules / 6",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        ModulesCatalog.modules.forEach { module ->
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = module.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        module.tasks.forEach { task ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = checked.contains(task.id),
                                    onCheckedChange = {
                                        scope.launch { store.toggleTask(today, task.id) }
                                    }
                                )
                                Text(text = task.label)
                            }
                        }

                        if (module.id == ModuleId.WRAP_UP) {
                            Spacer(modifier = Modifier.height(8.dp))
                            WrapUpBlock(
                                note = wrapUpNote,
                                mood = mood,
                                onNoteChange = { value ->
                                    scope.launch { store.updateWrapUp(today, value, mood) }
                                },
                                onMoodChange = { newMood ->
                                    scope.launch { store.updateWrapUp(today, wrapUpNote, newMood) }
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (completedModules >= 3) "今天已经打卡。" else "今天还可以慢慢补齐。",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WrapUpBlock(
    note: String,
    mood: Mood?,
    onNoteChange: (String) -> Unit,
    onMoodChange: (Mood?) -> Unit
) {
    OutlinedTextField(
        value = note,
        onValueChange = onNoteChange,
        label = { Text("一句话复盘") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MoodChip(label = "顺", selected = mood == Mood.SMOOTH) { onMoodChange(Mood.SMOOTH) }
        MoodChip(label = "一般", selected = mood == Mood.NORMAL) { onMoodChange(Mood.NORMAL) }
        MoodChip(label = "累", selected = mood == Mood.TIRED) { onMoodChange(Mood.TIRED) }
        MoodChip(label = "烦", selected = mood == Mood.ANNOYED) { onMoodChange(Mood.ANNOYED) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoodChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}
