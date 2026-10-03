package org.kde.kdeconnect.ui.remote.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.kde.kdeconnect.ui.remote.theme.RemoteTheme

@Composable
fun TextRemoteButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = RemoteTheme.dimensions.buttonSize,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 6.dp,
        animationSpec = tween(100),
        label = "elevation"
    )
    
    val offset by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = tween(100),
        label = "offset"
    )

    val shape = RoundedCornerShape(size / 4)
    val buttonColor = if (isPressed) RemoteTheme.colors.buttonPressed else RemoteTheme.colors.button

    Box(
        modifier = modifier
            .size(size)
            .offset(x = offset, y = offset)
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = RemoteTheme.colors.shadowDark,
                spotColor = RemoteTheme.colors.shadowDark
            )
            .clip(shape)
            .background(buttonColor)
            .border(1.dp, RemoteTheme.colors.shadowLight.copy(alpha = 0.5f), shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) RemoteTheme.colors.icon else RemoteTheme.colors.icon.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
