package org.kde.kdeconnect.ui.remote.components

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun Modifier.repeatingClick(
    interactionSource: MutableInteractionSource,
    enabled: Boolean = true,
    initialDelayMillis: Long = 350L,
    repeatIntervalMillis: Long = 70L,
    repeatHaptics: Boolean = false,
    onClick: () -> Unit
): Modifier = if (!enabled) this else this.composed {
    val context = LocalContext.current
    this.pointerInput(interactionSource, onClick) {
        coroutineScope {
            while (isActive) {
                val down = awaitPointerEventScope {
                    awaitFirstDown(requireUnconsumed = false)
                }
                val press = PressInteraction.Press(down.position)

                val pressJob = launch {
                    interactionSource.emit(press)
                    HapticManager.performPressHaptic(context)
                    onClick() // Initial click
                    delay(initialDelayMillis)
                    while (isActive) {
                        if (repeatHaptics) {
                            HapticManager.performTickHaptic(context)
                        }
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
                        HapticManager.performReleaseHaptic(context)
                    } else {
                        interactionSource.emit(PressInteraction.Cancel(press))
                    }
                }
            }
        }
    }
}
