package org.kde.kdeconnect.ui.remote

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.kde.kdeconnect.ui.remote.components.DPad
import org.kde.kdeconnect.ui.remote.components.KeyboardInput
import org.kde.kdeconnect.ui.remote.components.LabeledPillButton
import org.kde.kdeconnect.ui.remote.components.LedState
import org.kde.kdeconnect.ui.remote.components.RemoteBody
import org.kde.kdeconnect.ui.remote.components.RemoteButton
import org.kde.kdeconnect.ui.remote.components.StatusPill
import org.kde.kdeconnect.ui.remote.components.TextRemoteButton
import org.kde.kdeconnect.ui.remote.components.Touchpad
import org.kde.kdeconnect.ui.remote.components.VerticalVolumeBar
import org.kde.kdeconnect.ui.remote.controller.ConnectionState
import org.kde.kdeconnect.ui.remote.controller.RemoteViewModel
import org.kde.kdeconnect.ui.remote.theme.RemoteTheme

@Composable
fun RemoteScreen(
    viewModel: RemoteViewModel,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val connectionState by viewModel.connectionState.collectAsState()
    val deviceName by viewModel.deviceName.collectAsState()
    val pairingRequest by viewModel.pairingRequest.collectAsState()
    var showKeyboard by remember { mutableStateOf(false) }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = matches?.firstOrNull()
            if (!spokenText.isNullOrEmpty()) {
                Toast.makeText(context, "OpenClaw command: $spokenText", Toast.LENGTH_SHORT).show()
                viewModel.executeOpenClawVoiceCommand(spokenText)
            }
        }
    }

    if (pairingRequest != null) {
        val req = pairingRequest!!
        AlertDialog(
            onDismissRequest = { viewModel.rejectPairing(req.deviceId) },
            title = { Text("Pairing Request") },
            text = {
                Column {
                    Text("Device ${req.deviceName} wants to pair.")
                    if (req.verificationKey.isNotEmpty()) {
                        Text(
                            text = "Verification key: ${req.verificationKey}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.acceptPairing(req.deviceId) }
                ) {
                    Text("Accept")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.rejectPairing(req.deviceId) }
                ) {
                    Text("Reject")
                }
            }
        )
    }

    val showPasswordDialog by viewModel.showPasswordDialog.collectAsState()
    var inputPassword by remember { mutableStateOf("") }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissPasswordDialog() },
            title = { Text("Sudo Password") },
            text = {
                Column {
                    Text("Enter PC sudo password for shutdown:")
                    OutlinedTextField(
                        value = inputPassword,
                        onValueChange = { inputPassword = it },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.savePasswordAndShutdown(inputPassword)
                        inputPassword = ""
                    }
                ) {
                    Text("Save & Shutdown")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissPasswordDialog() }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    val ledState = when(connectionState) {
        ConnectionState.DISCONNECTED -> LedState.DISCONNECTED
        ConnectionState.CONNECTING -> LedState.CONNECTING
        ConnectionState.CONNECTED -> LedState.CONNECTED
        ConnectionState.ERROR -> LedState.ERROR
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        KeyboardInput(
            showKeyboard = showKeyboard,
            onKeyboardClosed = { showKeyboard = false },
            onKeyTyped = { text -> viewModel.typeText(text) },
            onEnter = { viewModel.select() },
            onBackspace = { viewModel.backspace() }
        )

        RemoteBody {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(RemoteTheme.dimensions.remotePadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                
                // Top Row: Power, Status Pill, Settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RemoteButton(
                        icon = rememberVectorPainter(Icons.Filled.PowerSettingsNew),
                        onClick = { viewModel.power() },
                        iconTint = RemoteTheme.colors.powerGlow,
                        size = 52.dp
                    )
                    
                    StatusPill(state = ledState, deviceName = deviceName)
                    
                    RemoteButton(
                        icon = rememberVectorPainter(Icons.Filled.Settings),
                        onClick = onNavigateToSettings,
                        size = 52.dp
                    )
                }
                
                // Second Row: Play, Mute, Voice
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LabeledPillButton(
                        icon = rememberVectorPainter(Icons.Filled.PlayArrow),
                        label = "Play",
                        onClick = { viewModel.playPause() },
                        modifier = Modifier.weight(1f)
                    )
                    
                    LabeledPillButton(
                        icon = rememberVectorPainter(Icons.AutoMirrored.Filled.VolumeOff),
                        label = "Mute",
                        onClick = { viewModel.mute() },
                        modifier = Modifier.weight(1f)
                    )

                    LabeledPillButton(
                        icon = rememberVectorPainter(Icons.Filled.Mic),
                        label = "Voice",
                        onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak command for OpenClaw...")
                            }
                            try {
                                speechRecognizerLauncher.launch(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Third Section: A, C, V (Left), D-Pad (Center), Volume Bar (Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Ctrl+A, Ctrl+C, Ctrl+V
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextRemoteButton(
                            text = "A",
                            onClick = { viewModel.ctrlA() },
                            size = 52.dp
                        )
                        TextRemoteButton(
                            text = "C",
                            onClick = { viewModel.ctrlC() },
                            size = 52.dp
                        )
                        TextRemoteButton(
                            text = "V",
                            onClick = { viewModel.ctrlV() },
                            size = 52.dp
                        )
                    }

                    // Center: D-Pad
                    DPad(
                        onUp = { viewModel.up() },
                        onDown = { viewModel.down() },
                        onLeft = { viewModel.left() },
                        onRight = { viewModel.right() },
                        onCenter = { viewModel.select() },
                        upIcon = rememberVectorPainter(Icons.Filled.KeyboardArrowUp),
                        downIcon = rememberVectorPainter(Icons.Filled.KeyboardArrowDown),
                        leftIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.KeyboardArrowLeft),
                        rightIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.KeyboardArrowRight)
                    )

                    // Right Column: Vertical Volume Bar (+, Speaker, -)
                    VerticalVolumeBar(
                        onVolumeUp = { viewModel.volumeUp() },
                        onVolumeDown = { viewModel.volumeDown() },
                        plusIcon = rememberVectorPainter(Icons.Filled.Add),
                        minusIcon = rememberVectorPainter(Icons.Filled.Remove),
                        speakerIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.VolumeUp)
                    )
                }
                
                // Fourth Row: Back, Keyboard, Home (Labeled Pill Buttons)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LabeledPillButton(
                        icon = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack),
                        label = "Back",
                        onClick = { viewModel.back() },
                        modifier = Modifier.weight(1f)
                    )
                    
                    LabeledPillButton(
                        icon = rememberVectorPainter(Icons.Filled.Keyboard),
                        label = "Keyboard",
                        onClick = { showKeyboard = !showKeyboard },
                        modifier = Modifier.weight(1f)
                    )
                    
                    LabeledPillButton(
                        icon = rememberVectorPainter(Icons.Filled.Home),
                        label = "Home",
                        onClick = { viewModel.home() },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Bottom: Touchpad
                Touchpad(
                    onScroll = { offset ->
                        viewModel.moveMouse(offset.x, offset.y)
                    },
                    onTap = { viewModel.leftClick() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
