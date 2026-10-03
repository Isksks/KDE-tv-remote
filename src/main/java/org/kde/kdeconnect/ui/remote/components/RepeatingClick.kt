package org.kde.kdeconnect.ui.remote.components

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun Modifier.repeatingClick(
    interactionSource: MutableInteractionSource,
    enabled: Boolean = true,
    initialDelayMillis: Long = 350L,
    repeatIntervalMillis: Long = 70L,
    onClick: () -> Unit
): Modifier = if (!enabled) this else this.pointerInput(interactionSource, onClick) {
    coroutineScope {
        while (isActive) {
            val down = awaitPointerEventScope {
                awaitFirstDown(requireUnconsumed = false)
            }
            val press = PressInteraction.Press(down.position)

            val pressJob = launch {
                interactionSource.emit(press)
                onClick() // Initial click
                delay(initialDelayMillis)
                while (isActive) {
                    onClick() // Repeating click
                    delay(repeatIntervalMillis)
                }
            }

            val up = awaitPointerEventScope {
                waitForUpOrCancellation()
            }
            pressJob.cancel()

            launch {
                if (up != null) {
                    interactionSource.emit(PressInteraction.Release(press))
                } else {
                    interactionSource.emit(PressInteraction.Cancel(press))
                }
            }
        }
    }
}
