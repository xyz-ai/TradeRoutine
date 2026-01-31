package com.traderoutine.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.traderoutine.data.ModuleDefinition
import com.traderoutine.data.ModuleId
import com.traderoutine.ui.AppViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: AppViewModel) {
    val record by viewModel.todayRecord.collectAsState()
    val completedCount = record.completedModules.size
    val isSuccess = completedCount >= 3

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("今日打卡", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isSuccess) "已完成 ≥3 个模块" else "完成 ${completedCount} / 6（目标 ≥3）",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        items(viewModel.modules.size) { index ->
            val module = viewModel.modules[index]
            ModuleCard(
                module = module,
                record = record,
                onToggle = { optionId ->
                    viewModel.toggleOption(record, module.id, optionId)
                },
                onRecapChange = { viewModel.updateRecap(record, it) },
                onRecapStatus = { viewModel.updateRecapStatus(record, it) }
            )
        }
    }
}

@Composable
private fun ModuleCard(
    module: ModuleDefinition,
    record: com.traderoutine.data.DayRecord,
    onToggle: (String) -> Unit,
    onRecapChange: (String) -> Unit,
    onRecapStatus: (String) -> Unit
) {
    val completed = record.completedModules.contains(module.id)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(module.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (completed) "已做" else "未做",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (completed) Color(0xFF16A34A) else MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(module.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(12.dp))

            if (module.options.isNotEmpty()) {
                module.options.forEach { option ->
                    val checked = record.selectedOptions[module.id].orEmpty().contains(option.id)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { onToggle(option.id) }
                        )
                        Text(option.label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (module.hasFreeText) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = record.recapText.orEmpty(),
                    onValueChange = onRecapChange,
                    placeholder = { Text("一句话复盘") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    module.statusOptions.forEach { status ->
                        val selected = record.recapStatus == status
                        TextButton(
                            onClick = { onRecapStatus(if (selected) "" else status) },
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(if (selected) "$status ✓" else status)
                        }
                    }
                }
            }
        }
    }
}
