package org.kde.kdeconnect.ui.remote.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kde.kdeconnect.ui.remote.theme.RemoteTheme

@Composable
fun DPad(
    onUp: () -> Unit,
    onDown: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onCenter: () -> Unit,
    upIcon: Painter,
    downIcon: Painter,
    leftIcon: Painter,
    rightIcon: Painter,
    modifier: Modifier = Modifier
) {
    val size = RemoteTheme.dimensions.dpadSize
    
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = RemoteTheme.colors.shadowDark,
                spotColor = RemoteTheme.colors.shadowDark
            )
            .clip(CircleShape)
            .background(RemoteTheme.colors.button)
            .border(1.dp, RemoteTheme.colors.shadowLight.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // UP
        val upInteractionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(size / 3)
                .clip(CircleShape)
                .repeatingClick(interactionSource = upInteractionSource, onClick = onUp),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = upIcon, contentDescription = "Up", tint = RemoteTheme.colors.icon)
        }

        // DOWN
        val downInteractionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(size / 3)
                .clip(CircleShape)
                .repeatingClick(interactionSource = downInteractionSource, onClick = onDown),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = downIcon, contentDescription = "Down", tint = RemoteTheme.colors.icon)
        }

        // LEFT
        val leftInteractionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(size / 3)
                .clip(CircleShape)
                .repeatingClick(interactionSource = leftInteractionSource, onClick = onLeft),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = leftIcon, contentDescription = "Left", tint = RemoteTheme.colors.icon)
        }

        // RIGHT
        val rightInteractionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(size / 3)
                .clip(CircleShape)
                .repeatingClick(interactionSource = rightInteractionSource, onClick = onRight),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = rightIcon, contentDescription = "Right", tint = RemoteTheme.colors.icon)
        }

        // CENTER BUTTON ("OK")
        val centerInteractionSource = remember { MutableInteractionSource() }
        val isCenterPressed by centerInteractionSource.collectIsPressedAsState()

        val centerElevation by animateDpAsState(
            targetValue = if (isCenterPressed) 1.dp else 6.dp,
            animationSpec = tween(100),
            label = "centerElevation"
        )
        
        val centerOffset by animateDpAsState(
            targetValue = if (isCenterPressed) 2.dp else 0.dp,
            animationSpec = tween(100),
            label = "centerOffset"
        )

        val centerColor = if (isCenterPressed) RemoteTheme.colors.buttonPressed else RemoteTheme.colors.button

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(size / 2.6f)
                .offset(x = centerOffset, y = centerOffset)
                .shadow(
                    elevation = centerElevation,
                    shape = CircleShape,
                    ambientColor = RemoteTheme.colors.shadowDark,
                    spotColor = RemoteTheme.colors.shadowDark
                )
                .clip(CircleShape)
                .background(centerColor)
                .border(1.dp, RemoteTheme.colors.shadowLight.copy(alpha = 0.5f), CircleShape)
                .repeatingClick(interactionSource = centerInteractionSource, onClick = onCenter),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "OK",
                color = RemoteTheme.colors.icon,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
