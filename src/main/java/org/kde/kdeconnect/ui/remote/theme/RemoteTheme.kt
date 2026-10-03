package org.kde.kdeconnect.ui.remote.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalRemoteColors = staticCompositionLocalOf { lightRemoteColors }
val LocalRemoteDimensions = staticCompositionLocalOf { defaultRemoteDimensions }

object RemoteTheme {
    val colors: RemoteColors
        @Composable
        @ReadOnlyComposable
        get() = LocalRemoteColors.current

    val dimensions: RemoteDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalRemoteDimensions.current
}

@Composable
fun RemoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkRemoteColors else lightRemoteColors
    val dimensions = defaultRemoteDimensions

    CompositionLocalProvider(
        LocalRemoteColors provides colors,
        LocalRemoteDimensions provides dimensions,
        content = content
    )
}
