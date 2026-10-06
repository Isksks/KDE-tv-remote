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
    var textValue by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(showKeyboard) {
        if (showKeyboard) {
            textValue = TextFieldValue("")
            delay(100)
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    if (showKeyboard) {
        Box(modifier = Modifier.size(1.dp).alpha(0f)) {
            TextField(
                value = textValue,
                onValueChange = { newValue ->
                    val oldText = textValue.text
                    val newText = newValue.text

                    if (newText.endsWith("\n")) {
                        val diff = newText.dropLast(1)
                        if (diff.length > oldText.length) {
                            val added = diff.substring(oldText.length)
                            onKeyTyped(added)
                        }
                        onEnter()
                        textValue = TextFieldValue("")
                    } else {
                        var commonPrefixLen = 0
                        val maxPrefix = minOf(oldText.length, newText.length)
                        while (commonPrefixLen < maxPrefix && oldText[commonPrefixLen] == newText[commonPrefixLen]) {
                            commonPrefixLen++
                        }

                        val deleteCount = oldText.length - commonPrefixLen
                        if (deleteCount > 0) {
                            repeat(deleteCount) { onBackspace() }
                        }

                        val added = newText.substring(commonPrefixLen)
                        if (added.isNotEmpty()) {
                            onKeyTyped(added)
                        }

                        textValue = newValue
                    }
                },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Backspace) {
                            if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                                if (textValue.text.isEmpty()) {
                                    onBackspace()
                                }
                            }
                            true
                        } else if (keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter) {
                            if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                                onEnter()
                                textValue = TextFieldValue("")
                            }
                            true
                        } else {
                            false
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send,
                    autoCorrect = true
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onEnter()
                        textValue = TextFieldValue("")
                    }
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
