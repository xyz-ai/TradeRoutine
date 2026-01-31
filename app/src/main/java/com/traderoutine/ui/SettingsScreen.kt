package com.traderoutine.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.traderoutine.data.SettingsState
import com.traderoutine.data.TradeRoutineStore
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(store: TradeRoutineStore, settings: SettingsState) {
    val scope = rememberCoroutineScope()
    var showConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "设置", style = MaterialTheme.typography.headlineSmall)
        SettingRow(
            label = "打卡提醒",
            checked = settings.reminderEnabled
        ) { enabled ->
            scope.launch {
                store.updateSettings { it.copy(reminderEnabled = enabled) }
            }
        }
        SettingRow(
            label = "深色模式",
            checked = settings.darkMode
        ) { enabled ->
            scope.launch { store.updateSettings { it.copy(darkMode = enabled) } }
        }
        SettingRow(
            label = "周末是否计入",
            checked = settings.weekendIncluded
        ) { enabled ->
            scope.launch { store.updateSettings { it.copy(weekendIncluded = enabled) } }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { showConfirm = true }) {
            Text(text = "清空本地数据（危险操作）", color = MaterialTheme.colorScheme.error)
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("确认清空？") },
            text = { Text("此操作会删除所有本地记录，且不可恢复。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch { store.clearAll() }
                        showConfirm = false
                    }
                ) {
                    Text("确认清空")
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
private fun SettingRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
