package com.traderoutine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.traderoutine.ui.AppViewModel

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, viewModel: AppViewModel) {
    val settings by viewModel.settings.collectAsState()
    var showConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("设置", style = MaterialTheme.typography.headlineSmall)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingRow(
                    title = "打卡提醒",
                    checked = settings.reminderEnabled,
                    onCheckedChange = { enabled ->
                        viewModel.updateSettings { it.copy(reminderEnabled = enabled) }
                    }
                )
                SettingRow(
                    title = "深色模式",
                    checked = settings.darkModeEnabled,
                    onCheckedChange = { enabled ->
                        viewModel.updateSettings { it.copy(darkModeEnabled = enabled) }
                    }
                )
                SettingRow(
                    title = "周末计入连续",
                    checked = settings.weekendIncluded,
                    onCheckedChange = { enabled ->
                        viewModel.updateSettings { it.copy(weekendIncluded = enabled) }
                    }
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("清空本地数据", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showConfirm = true }) {
                    Text("清空")
                }
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("确认清空？") },
            text = { Text("此操作不可恢复。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAll()
                    showConfirm = false
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun SettingRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
