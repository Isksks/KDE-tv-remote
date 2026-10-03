package org.kde.kdeconnect.ui.remote.controller

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.kde.kdeconnect.Device
import org.kde.kdeconnect.KdeConnect
import org.kde.kdeconnect.PairingHandler
import org.kde.kdeconnect.plugins.clipboard.ClipboardPlugin

enum class ConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED, ERROR
}

class RemoteViewModel(application: Application) : AndroidViewModel(application), KdeConnect.DeviceListChangedCallback {

    private val controller = RemoteController()

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _deviceName = MutableStateFlow("No Device")
    val deviceName: StateFlow<String> = _deviceName

    data class PairingRequest(val deviceId: String, val deviceName: String, val verificationKey: String)
    private val _pairingRequest = MutableStateFlow<PairingRequest?>(null)
    val pairingRequest: StateFlow<PairingRequest?> = _pairingRequest

    private val prefs = PreferenceManager.getDefaultSharedPreferences(application)

    private val _showPasswordDialog = MutableStateFlow(false)
    val showPasswordDialog: StateFlow<Boolean> = _showPasswordDialog

    fun onPowerButtonClicked() {
        val savedPassword = prefs.getString("shutdown_password", null)
        if (savedPassword.isNullOrEmpty()) {
            _showPasswordDialog.value = true
        } else {
            executeShutdownSequence(savedPassword)
        }
    }

    fun savePasswordAndShutdown(password: String) {
        prefs.edit().putString("shutdown_password", password).apply()
        _showPasswordDialog.value = false
        executeShutdownSequence(password)
    }

    fun dismissPasswordDialog() {
        _showPasswordDialog.value = false
    }

    private fun executeShutdownSequence(password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Send Ctrl+Alt+T first (terminal hotkey on Ubuntu/GNOME)
            controller.openTerminal()
            delay(400)

            // Send Alt+F2 (Run dialog on KDE Plasma/other Linux desktops)
            controller.openRunDialog()
            delay(400)

            // Launch terminal explicitly
            controller.typeText("konsole")
            controller.select()
            delay(1000) // Wait for terminal window to launch and gain focus

            // Send "sudo shutdown now" + Enter
            controller.typeText("sudo shutdown now")
            controller.select()
            delay(800) // Wait for sudo password prompt

            // Send password + Enter
            controller.typeText(password)
            controller.select()
        }
    }

    init {
        KdeConnect.getInstance().addDeviceListChangedCallback("RemoteViewModel", this)
        refreshDevice()
    }

    override fun onCleared() {
        super.onCleared()
        KdeConnect.getInstance().removeDeviceListChangedCallback("RemoteViewModel")
    }

    override fun onDeviceListChanged() {
        refreshDevice()
    }

    private fun refreshDevice() {
        val devices = KdeConnect.getInstance().devices.values
        val activeDevice = devices.firstOrNull { it.isPaired && it.isReachable }
        
        if (activeDevice != null) {
            _connectionState.value = ConnectionState.CONNECTED
            _deviceName.value = activeDevice.name
            controller.setDeviceId(activeDevice.deviceId)
        } else {
            _connectionState.value = ConnectionState.DISCONNECTED
            _deviceName.value = "Disconnected"
            controller.setDeviceId(null)
        }

        val requestingDevice = devices.firstOrNull { it.pairStatus == PairingHandler.PairState.RequestedByPeer }
        if (requestingDevice != null) {
            _pairingRequest.value = PairingRequest(
                deviceId = requestingDevice.deviceId,
                deviceName = requestingDevice.name,
                verificationKey = requestingDevice.verificationKey ?: ""
            )
        } else {
            _pairingRequest.value = null
        }
    }

    fun acceptPairing(deviceId: String) {
        KdeConnect.getInstance().getDevice(deviceId)?.acceptPairing()
        refreshDevice()
    }

    fun rejectPairing(deviceId: String) {
        KdeConnect.getInstance().getDevice(deviceId)?.cancelPairing()
        refreshDevice()
    }
    
    fun getDeviceId(): String? = controller.getDeviceId()
    
    fun sendClipboard() {
        controller.syncClipboard()
    }
    
    fun syncClipboard() {
        controller.syncClipboard()
    }
    
    // Pass-through to controller
    fun moveMouse(dx: Float, dy: Float) = controller.moveMouse(dx, dy)
    fun leftClick() = controller.leftClick()
    fun rightClick() = controller.rightClick()
    fun doubleClick() = controller.doubleClick()
    fun scroll(amount: Float) = controller.scroll(amount)
    
    fun up() = controller.up()
    fun down() = controller.down()
    fun left() = controller.left()
    fun right() = controller.right()
    fun select() = controller.select()
    fun typeText(text: String) = controller.typeText(text)
    fun backspace() = controller.backspace()
    
    fun ctrlA() = controller.ctrlA()
    fun ctrlC() = controller.ctrlC()
    fun ctrlV() = controller.ctrlV()
    
    fun home() = controller.home()
    fun back() = controller.back()
    fun power() = onPowerButtonClicked()
    
    fun volumeUp() = controller.volumeUp()
    fun volumeDown() = controller.volumeDown()
    fun mute() = controller.mute()
    fun playPause() = controller.playPause()
}
