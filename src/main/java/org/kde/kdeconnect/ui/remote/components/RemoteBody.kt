package org.kde.kdeconnect.ui.remote.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import org.kde.kdeconnect.ui.remote.theme.RemoteTheme

@Composable
fun RemoteBody(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(RemoteTheme.dimensions.remoteCornerRadius)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(RemoteTheme.dimensions.remotePadding)
            .shadow(
                elevation = 16.dp,
                shape = shape,
                ambientColor = RemoteTheme.colors.shadowDark,
                spotColor = RemoteTheme.colors.shadowDark
            )
            .clip(shape)
            .background(RemoteTheme.colors.remoteBody)
            .border(1.dp, RemoteTheme.colors.shadowLight.copy(alpha = 0.3f), shape)
            .padding(16.dp),
        content = content
    )
}
