package org.kde.kdeconnect.ui.remote.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class RemoteDimensions(
    val remoteCornerRadius: Dp = 32.dp,
    val buttonSize: Dp = 64.dp,
    val smallButtonSize: Dp = 48.dp,
    val dpadSize: Dp = 180.dp,
    val touchpadHeight: Dp = 200.dp,
    val touchpadCornerRadius: Dp = 16.dp,
    val remotePadding: Dp = 16.dp,
    val buttonSpacing: Dp = 12.dp
)

val defaultRemoteDimensions = RemoteDimensions()
