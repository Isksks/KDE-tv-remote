package org.kde.kdeconnect.ui.remote.components

import android.view.KeyEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun KeyboardInput(
    showKeyboard: Boolean,
    onKeyboardClosed: () -> Unit,
    onKeyTyped: (String) -> Unit,
    onEnter: () -> Unit,
    onBackspace: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    
    // Using TextFieldValue allows us to precisely control the selection cursor.
    // We maintain a dummy character so the soft keyboard always allows backspace.
    var textValue by remember { mutableStateOf(TextFieldValue(" ", selection = TextRange(1))) }

    if (showKeyboard) {
        LaunchedEffect(Unit) {
            delay(100)
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        Box(modifier = Modifier.size(1.dp).alpha(0f)) {
            TextField(
                value = textValue,
                onValueChange = { newValue ->
                    val oldText = textValue.text
                    val newText = newValue.text

                    if (newText.length < oldText.length) {
                        // Deletion detected
                        val deleteCount = oldText.length - newText.length
                        repeat(deleteCount) { onBackspace() }
                    } else if (newText.length > oldText.length) {
                        // Addition detected
                        val diff = newText.substring(oldText.length)
                        if (diff == "\n") {
                            onEnter()
                        } else {
                            onKeyTyped(diff)
                        }
                    }
                    
                    // Reset to dummy text so backspace always works and cursor stays at end
                    textValue = TextFieldValue(" ", selection = TextRange(1))
                },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    // Explicitly catch hardware/software key events as a fallback
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Backspace) {
                            if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                                onBackspace()
                            }
                            true
                        } else if (keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter) {
                            if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                                onEnter()
                            }
                            true
                        } else {
                            false
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send,
                    autoCorrect = false
                ),
                keyboardActions = KeyboardActions(
                    onSend = { onEnter() }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}
