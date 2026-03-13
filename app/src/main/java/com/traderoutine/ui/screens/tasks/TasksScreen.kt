package com.traderoutine.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.traderoutine.model.DailyStatus
import com.traderoutine.model.DailyTaskItem
import com.traderoutine.ui.strings.LocalAppStrings
import com.traderoutine.ui.strings.statusText
import com.traderoutine.ui.theme.CalmAccentContainer
import com.traderoutine.ui.theme.CalmNotStarted
import com.traderoutine.ui.theme.CalmSuccessContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    onAddTask: () -> Unit,
    onEditTask: (Long) -> Unit,
) {
    val strings = LocalAppStrings.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dialogMessage by viewModel.dialogMessage.collectAsStateWithLifecycle()
    var deleteTarget by remember { mutableStateOf<DailyTaskItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = strings.todaysRoutine)
                        Text(
                            text = strings.dateFormatter(uiState.date),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = strings.addTask
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            StatusHeader(
                summary = strings.calmSummaryPrefix,
                status = uiState.status,
                totalCount = uiState.totalCount,
                completedCount = uiState.completedCount
            )
            if (uiState.isEmpty) {
                EmptyTasksState(onAddTask = onAddTask)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tasks, key = { it.templateId }) { task ->
                        TaskCard(
                            task = task,
                            onCheckedChange = { checked -> viewModel.onTaskCheckedChange(task, checked) },
                            onEdit = { onEditTask(task.templateId) },
                            onDelete = { deleteTarget = task }
                        )
                    }
                }
            }
        }
    }

    if (dialogMessage != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDialog,
            title = { Text(text = strings.completedDialogTitle) },
            text = { Text(text = dialogMessage.orEmpty()) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissDialog) {
                    Text(text = strings.confirm)
                }
            }
        )
    }

    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(text = strings.deleteTaskConfirmTitle) },
            text = { Text(text = strings.deleteTaskConfirmBody) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTemplate(deleteTarget!!.templateId)
                        deleteTarget = null
                    }
                ) {
                    Text(text = strings.deleteTask)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text(text = strings.cancel)
                }
            }
        )
    }
}

@Composable
private fun StatusHeader(
    summary: String,
    status: DailyStatus,
    totalCount: Int,
    completedCount: Int,
) {
    val strings = LocalAppStrings.current
    val background = when (status) {
        DailyStatus.NOT_STARTED -> CalmNotStarted
        DailyStatus.IN_PROGRESS -> CalmAccentContainer
        DailyStatus.COMPLETED -> CalmSuccessContainer
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        shape = MaterialTheme.shapes.large,
        color = background,
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = summary, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = strings.statusText(status),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = strings.completedCountFormat(completedCount, totalCount),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EmptyTasksState(onAddTask: () -> Unit) {
    val strings = LocalAppStrings.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = strings.noTasksTitle,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = strings.noTasksBody,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            TextButton(onClick = onAddTask) {
                Text(text = strings.addTask)
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: DailyTaskItem,
    onCheckedChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onCheckedChange
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 8.dp)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier.alpha(if (task.isCompleted) 0.7f else 1f)
                    )
                    OptionalText(task.note)
                    TimeLine(
                        durationText = task.durationText,
                        startTime = task.startTime,
                        endTime = task.endTime
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = LocalAppStrings.current.editTask
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = LocalAppStrings.current.deleteTask
                    )
                }
            }
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (task.isCompleted) LocalAppStrings.current.finishedLabel else LocalAppStrings.current.unfinishedLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${task.orderIndex + 1}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun OptionalText(value: String) {
    if (value.isBlank()) return
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun TimeLine(
    durationText: String,
    startTime: String,
    endTime: String,
) {
    val hasDuration = durationText.isNotBlank()
    val hasTimeRange = startTime.isNotBlank() || endTime.isNotBlank()
    if (!hasDuration && !hasTimeRange) return

    Spacer(modifier = Modifier.height(8.dp))
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (hasDuration) {
            Text(
                text = durationText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (hasTimeRange) {
            val renderedTime = listOf(startTime, endTime)
                .filter { it.isNotBlank() }
                .joinToString(" - ")
            Text(
                text = renderedTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
