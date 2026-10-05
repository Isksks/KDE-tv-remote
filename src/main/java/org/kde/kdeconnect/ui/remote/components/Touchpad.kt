package org.kde.kdeconnect.ui.remote.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.kde.kdeconnect.ui.remote.theme.RemoteTheme

@Composable
fun Touchpad(
    onScroll: (Offset) -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(24.dp)
    val accentColor = RemoteTheme.colors.icon.copy(alpha = 0.35f)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        RemoteTheme.colors.innerShadowDark.copy(alpha = 0.15f),
                        RemoteTheme.colors.touchpad,
                        RemoteTheme.colors.innerShadowLight.copy(alpha = 0.15f)
                    )
                )
            )
            .border(1.5.dp, RemoteTheme.colors.shadowLight.copy(alpha = 0.4f), shape)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onScroll(dragAmount)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        HapticManager.performPressHaptic(context)
                        onTap()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Corner bracket accents ⌜ ⌝ ⌞ ⌟
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val bracketLength = 16.dp.toPx()
            val bracketRadius = 8.dp.toPx()
            val strokeWidth = 2.dp.toPx()

            // Top-Left ⌜
            val topLeftPath = Path().apply {
                moveTo(0f, bracketLength)
                lineTo(0f, bracketRadius)
                quadraticTo(0f, 0f, bracketRadius, 0f)
                lineTo(bracketLength, 0f)
            }
            drawPath(topLeftPath, accentColor, style = Stroke(strokeWidth))

            // Top-Right ⌝
            val topRightPath = Path().apply {
                moveTo(size.width - bracketLength, 0f)
                lineTo(size.width - bracketRadius, 0f)
                quadraticTo(size.width, 0f, size.width, bracketRadius)
                lineTo(size.width, bracketLength)
            }
            drawPath(topRightPath, accentColor, style = Stroke(strokeWidth))

            // Bottom-Left ⌞
            val bottomLeftPath = Path().apply {
                moveTo(0f, size.height - bracketLength)
                lineTo(0f, size.height - bracketRadius)
                quadraticTo(0f, size.height, bracketRadius, size.height)
                lineTo(bracketLength, size.height)
            }
            drawPath(bottomLeftPath, accentColor, style = Stroke(strokeWidth))

            // Bottom-Right ⌟
            val bottomRightPath = Path().apply {
                moveTo(size.width - bracketLength, size.height)
                lineTo(size.width - bracketRadius, size.height)
                quadraticTo(size.width, size.height, size.width, size.height - bracketRadius)
                lineTo(size.width, size.height - bracketLength)
            }
            drawPath(bottomRightPath, accentColor, style = Stroke(strokeWidth))
        }

        // Bottom center horizontal handle bar —
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .size(width = 36.dp, height = 4.dp)
                .background(accentColor, RoundedCornerShape(2.dp))
        )
    }
}
