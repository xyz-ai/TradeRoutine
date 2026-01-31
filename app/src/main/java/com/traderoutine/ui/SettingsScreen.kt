package com.traderoutine.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(viewModel: MainViewModel, contentPadding: PaddingValues) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier
            .padding(
                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                end = contentPadding.calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                top = contentPadding.calculateTopPadding() + 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingToggle(
                    title = "打卡提醒",
                    checked = settings.reminderEnabled,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(reminderEnabled = checked) }
                    },
                )
                SettingToggle(
                    title = "深色模式",
                    checked = settings.darkModeEnabled,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(darkModeEnabled = checked) }
                    },
                )
                SettingToggle(
                    title = "周末计入",
                    checked = settings.includeWeekend,
                    onCheckedChange = { checked ->
                        viewModel.updateSettings { it.copy(includeWeekend = checked) }
                    },
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "清空本地数据（危险操作）",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "会清除所有打卡记录与设置，无法恢复。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = viewModel::clearAll) {
                    Text(text = "清空数据")
                }
            }
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
