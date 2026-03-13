package com.traderoutine.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.traderoutine.BuildConfig
import com.traderoutine.model.UiLanguage
import com.traderoutine.ui.strings.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onOpenAbout: () -> Unit,
    onOpenDisclaimer: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = strings.settingsTab) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsSection(title = strings.language) {
                LanguageRow(
                    label = strings.languageEnglish,
                    selected = uiState.language == UiLanguage.ENGLISH,
                    onClick = { viewModel.setLanguage(UiLanguage.ENGLISH) }
                )
                LanguageRow(
                    label = strings.languageChinese,
                    selected = uiState.language == UiLanguage.CHINESE,
                    onClick = { viewModel.setLanguage(UiLanguage.CHINESE) }
                )
            }

            SettingsSection(title = strings.clearLocalData) {
                SettingsActionRow(
                    title = strings.clearLocalData,
                    subtitle = strings.clearLocalDataBody,
                    onClick = { showClearDialog = true }
                )
            }

            SettingsSection(title = strings.aboutApp) {
                SettingsActionRow(
                    title = strings.aboutApp,
                    subtitle = "${strings.versionLabel} ${BuildConfig.VERSION_NAME}",
                    onClick = onOpenAbout
                )
                SettingsActionRow(
                    title = strings.disclaimer,
                    subtitle = null,
                    onClick = onOpenDisclaimer
                )
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(text = strings.clearLocalData) },
            text = { Text(text = strings.clearLocalDataBody) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearLocalData()
                        showClearDialog = false
                    }
                ) {
                    Text(text = strings.confirm)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(text = strings.cancel)
                }
            }
        )
    }
}

@Composable
fun AboutScreen(onNavigateBack: () -> Unit) {
    val strings = LocalAppStrings.current
    InfoScreen(
        title = strings.aboutApp,
        onNavigateBack = onNavigateBack,
        body = listOf(
            strings.appName,
            "${strings.versionLabel} ${BuildConfig.VERSION_NAME}",
            strings.aboutDescription
        )
    )
}

@Composable
fun DisclaimerScreen(onNavigateBack: () -> Unit) {
    val strings = LocalAppStrings.current
    InfoScreen(
        title = strings.disclaimer,
        onNavigateBack = onNavigateBack,
        body = listOf(strings.disclaimerBody)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoScreen(
    title: String,
    onNavigateBack: () -> Unit,
    body: List<String>,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = LocalAppStrings.current.cancel
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                body.forEach { paragraph ->
                    Text(
                        text = paragraph,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 1.dp
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun LanguageRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            modifier = Modifier.padding(start = 12.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                modifier = Modifier.padding(6.dp)
            )
        }
    }
}
