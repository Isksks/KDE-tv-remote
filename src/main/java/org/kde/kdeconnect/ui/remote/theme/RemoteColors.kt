package org.kde.kdeconnect.ui.remote.theme

import androidx.compose.ui.graphics.Color

data class RemoteColors(
    val background: Color,
    val remoteBody: Color,
    val button: Color,
    val buttonPressed: Color,
    val text: Color,
    val icon: Color,
    val shadowLight: Color,
    val shadowDark: Color,
    val innerShadowLight: Color,
    val innerShadowDark: Color,
    val accent: Color,
    val touchpad: Color,
    val ledConnected: Color,
    val ledConnecting: Color,
    val ledError: Color,
    val ledDisconnected: Color,
    val powerGlow: Color
)

val lightRemoteColors = RemoteColors(
    background = Color(0xFFE8F0F8),
    remoteBody = Color(0xFFE8F0F8),
    button = Color(0xFFF2F7FD),
    buttonPressed = Color(0xFFDAE5F0),
    text = Color(0xFF334155),
    icon = Color(0xFF1E293B),
    shadowLight = Color(0xFFFFFFFF),
    shadowDark = Color(0xFFC3D2E2),
    innerShadowLight = Color(0xFFFFFFFF),
    innerShadowDark = Color(0xFFC3D2E2),
    accent = Color(0xFF2563EB),
    touchpad = Color(0xFFDFE9F5),
    ledConnected = Color(0xFF22C55E),
    ledConnecting = Color(0xFF3B82F6),
    ledError = Color(0xFFEF4444),
    ledDisconnected = Color(0xFF94A3B8),
    powerGlow = Color(0xFFEF4444)
)

val darkRemoteColors = RemoteColors(
    background = Color(0xFF0F1115),
    remoteBody = Color(0xFF0F1115),
    button = Color(0xFF1B1D22),
    buttonPressed = Color(0xFF141519),
    text = Color(0xFFE2E8F0),
    icon = Color(0xFFF1F5F9),
    shadowLight = Color(0xFF292C34),
    shadowDark = Color(0xFF07080A),
    innerShadowLight = Color(0xFF292C34),
    innerShadowDark = Color(0xFF07080A),
    accent = Color(0xFF38BDF8),
    touchpad = Color(0xFF15171C),
    ledConnected = Color(0xFF22C55E),
    ledConnecting = Color(0xFF38BDF8),
    ledError = Color(0xFFEF4444),
    ledDisconnected = Color(0xFF64748B),
    powerGlow = Color(0xFFEF4444)
)
