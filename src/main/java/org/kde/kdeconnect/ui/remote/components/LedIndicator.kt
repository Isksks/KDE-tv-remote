package org.kde.kdeconnect.ui.remote.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import org.kde.kdeconnect.ui.remote.theme.RemoteTheme

enum class LedState {
    CONNECTING, CONNECTED, ERROR, DISCONNECTED
}

@Composable
fun LedIndicator(
    state: LedState,
    modifier: Modifier = Modifier
) {
    val targetColor = when (state) {
        LedState.CONNECTING -> RemoteTheme.colors.ledConnecting
        LedState.CONNECTED -> RemoteTheme.colors.ledConnected
        LedState.ERROR -> RemoteTheme.colors.ledError
        LedState.DISCONNECTED -> RemoteTheme.colors.ledDisconnected
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(500),
        label = "ledColor"
    )

    Box(
        modifier = modifier
            .size(12.dp)
            .shadow(
                elevation = if (state != LedState.DISCONNECTED) 4.dp else 1.dp,
                shape = CircleShape,
                ambientColor = animatedColor,
                spotColor = animatedColor
            )
            .clip(CircleShape)
            .background(animatedColor)
            .border(1.dp, RemoteTheme.colors.shadowDark.copy(alpha = 0.5f), CircleShape)
    )
}
