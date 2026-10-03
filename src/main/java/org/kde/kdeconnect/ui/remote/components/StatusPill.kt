package org.kde.kdeconnect.ui.remote.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kde.kdeconnect.ui.remote.theme.RemoteTheme

@Composable
fun StatusPill(
    state: LedState,
    deviceName: String,
    modifier: Modifier = Modifier
) {
    val ledColor = when (state) {
        LedState.CONNECTING -> RemoteTheme.colors.ledConnecting
        LedState.CONNECTED -> RemoteTheme.colors.ledConnected
        LedState.ERROR -> RemoteTheme.colors.ledError
        LedState.DISCONNECTED -> RemoteTheme.colors.ledDisconnected
    }

    val animatedColor by animateColorAsState(
        targetValue = ledColor,
        animationSpec = tween(500),
        label = "ledColor"
    )

    val statusText = when (state) {
        LedState.CONNECTED -> if (deviceName.isNotEmpty() && deviceName != "Disconnected") deviceName else "Connected"
        LedState.CONNECTING -> "Connecting..."
        LedState.ERROR -> "Error"
        LedState.DISCONNECTED -> "Disconnected"
    }

    val shape = RoundedCornerShape(24.dp)

    Row(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = shape,
                ambientColor = RemoteTheme.colors.shadowDark,
                spotColor = RemoteTheme.colors.shadowDark
            )
            .clip(shape)
            .background(RemoteTheme.colors.button)
            .border(1.dp, RemoteTheme.colors.shadowLight.copy(alpha = 0.5f), shape)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(animatedColor)
        )
        Text(
            text = statusText,
            color = RemoteTheme.colors.text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
