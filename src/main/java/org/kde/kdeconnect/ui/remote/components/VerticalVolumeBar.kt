package org.kde.kdeconnect.ui.remote.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import org.kde.kdeconnect.ui.remote.theme.RemoteTheme

@Composable
fun VerticalVolumeBar(
    onVolumeUp: () -> Unit,
    onVolumeDown: () -> Unit,
    plusIcon: Painter,
    minusIcon: Painter,
    speakerIcon: Painter,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    val width = 56.dp

    Column(
        modifier = modifier
            .width(width)
            .shadow(
                elevation = 6.dp,
                shape = shape,
                ambientColor = RemoteTheme.colors.shadowDark,
                spotColor = RemoteTheme.colors.shadowDark
            )
            .clip(shape)
            .background(RemoteTheme.colors.button)
            .border(1.dp, RemoteTheme.colors.shadowLight.copy(alpha = 0.5f), shape)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // PLUS BUTTON (Volume Up)
        val upInteractionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .repeatingClick(
                    interactionSource = upInteractionSource,
                    onClick = onVolumeUp
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = plusIcon, contentDescription = "Volume Up", tint = RemoteTheme.colors.icon)
        }

        // SPEAKER ICON (Middle Indicator)
        Icon(
            painter = speakerIcon,
            contentDescription = "Volume",
            tint = RemoteTheme.colors.icon.copy(alpha = 0.7f),
            modifier = Modifier
                .padding(vertical = 12.dp)
                .size(20.dp)
        )

        // MINUS BUTTON (Volume Down)
        val downInteractionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .repeatingClick(
                    interactionSource = downInteractionSource,
                    onClick = onVolumeDown
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = minusIcon, contentDescription = "Volume Down", tint = RemoteTheme.colors.icon)
        }
    }
}
