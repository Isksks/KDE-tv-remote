package org.kde.kdeconnect.ui.remote.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import org.kde.kdeconnect.ui.remote.theme.RemoteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToSendFile: () -> Unit,
    onManagePermissions: () -> Unit,
    isDarkMode: Boolean,
    onDarkModeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = RemoteTheme.colors.text) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = RemoteTheme.colors.icon)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RemoteTheme.colors.background
                )
            )
        },
        containerColor = RemoteTheme.colors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            SettingsCategory("GENERAL")
            SettingsItem(
                title = "Send File",
                subtitle = "Send files to connected device",
                icon = "📁",
                onClick = onNavigateToSendFile
            )
            SettingsItem(
                title = "Permissions & Auto-Revoke",
                subtitle = "Disable 'Remove permissions if app is unused'",
                icon = "🔒",
                onClick = onManagePermissions
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsCategory("APPEARANCE")
            SettingsToggleItem(
                title = "Dark Mode",
                subtitle = "Toggle dark/light theme",
                icon = "🌙",
                checked = isDarkMode,
                onCheckedChange = onDarkModeChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsCategory("DEVELOPER")
            val uriHandler = LocalUriHandler.current
            SettingsItem(
                title = "Connect with me",
                subtitle = "Anurag Kumar on LinkedIn",
                icon = "🔗",
                onClick = {
                    uriHandler.openUri("https://www.linkedin.com/in/anurag-kumar-47271335a/?isSelfProfile=true")
                }
            )
            SettingsItem(
                title = "GitHub Profile",
                subtitle = "github.com/Isksks",
                icon = "💻",
                onClick = {
                    uriHandler.openUri("https://github.com/Isksks")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsCategory("MANUAL & BUTTON GUIDE")
            ManualItem("🔴 Power Button", "Opens terminal & executes sudo shutdown with your saved password.")
            ManualItem("🟢 Status Pill", "Displays real-time device connection status and name.")
            ManualItem("⚙️ Settings Button", "Opens app options, send files, dark mode, and manual.")
            ManualItem("▶️ Play Button", "Toggles Play / Pause on PC media players.")
            ManualItem("🔇 Mute Button", "Toggles system audio mute / sound zero on PC.")
            ManualItem("🎙️ Voice Button", "Captures voice command: opens apps directly ('open youtube') or executes OpenClaw.")
            ManualItem("🅰️ Button A", "Sends Ctrl+A (Select All) to active window.")
            ManualItem("Ⓒ Button C", "Sends Ctrl+C (Copy) to active window.")
            ManualItem("Ⓥ Button V", "Auto-syncs phone clipboard & sends Ctrl+V (Paste).")
            ManualItem("🎯 D-Pad & OK", "Arrow keys & Select. Supports auto-repeat on hold!")
            ManualItem("🔊 Volume Bar", "+ / - volume control. Supports auto-repeat on hold!")
            ManualItem("⬅️ Back Button", "Sends Alt+F4 to close current window.")
            ManualItem("⌨️ Keyboard Button", "Toggles soft keyboard for typing text & enter/backspace.")
            ManualItem("🏠 Home Button", "Sends Super / Meta key to open app launcher.")
            ManualItem("🖐️ Touchpad", "Controls PC mouse cursor smoothly with tap-to-click.")

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        color = RemoteTheme.colors.accent,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, modifier = Modifier.padding(end = 16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = RemoteTheme.colors.text)
            Text(
                text = subtitle, 
                color = RemoteTheme.colors.text.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = RemoteTheme.colors.icon)
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    icon: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, modifier = Modifier.padding(end = 16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = RemoteTheme.colors.text)
            Text(
                text = subtitle, 
                color = RemoteTheme.colors.text.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = RemoteTheme.colors.accent,
                checkedTrackColor = RemoteTheme.colors.buttonPressed,
                uncheckedThumbColor = RemoteTheme.colors.text.copy(alpha = 0.6f),
                uncheckedTrackColor = RemoteTheme.colors.button
            )
        )
    }
}

@Composable
fun ManualItem(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = RemoteTheme.colors.text,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = description,
            color = RemoteTheme.colors.text.copy(alpha = 0.65f),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
