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
import java.net.URLEncoder

sealed class VoiceAction {
    data class YouTubeSearch(val query: String) : VoiceAction()
    object OpenYouTube : VoiceAction()
    object OpenTerminal : VoiceAction()
    data class BrowserSearch(val query: String, val browser: String) : VoiceAction()
    data class OpenApp(val appName: String) : VoiceAction()
    data class OpenClaw(val rawCommand: String) : VoiceAction()
}

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

    private fun normalizeVoiceText(s: String): String {
        return s.lowercase()
            .replace("you tube", "youtube")
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun parseVoiceAction(raw: String): VoiceAction {
        val t = normalizeVoiceText(raw)
        if (t.isEmpty()) return VoiceAction.OpenClaw(raw)

        val browserPattern = "(chrome|firefox|brave|edge|opera|safari|chromium|vivaldi|browser)"

        // 1. Specific YouTube Searches
        Regex("""(?:search|play|find)\s+(.+?)\s+(?:on|in)\s+youtube""").find(t)?.let { match ->
            val query = match.groupValues.getOrNull(1)?.trim().orEmpty()
            if (query.isNotEmpty()) return VoiceAction.YouTubeSearch(query)
        }
        Regex("""youtube\s+(?:search|for)\s+(.+)""").find(t)?.let { match ->
            val query = match.groupValues.getOrNull(1)?.trim().orEmpty()
            if (query.isNotEmpty()) return VoiceAction.YouTubeSearch(query)
        }
        Regex("""open youtube\s+and\s+search\s+(?:for\s+)?(.+)""").find(t)?.let { match ->
            val query = match.groupValues.getOrNull(1)?.trim().orEmpty()
            if (query.isNotEmpty()) return VoiceAction.YouTubeSearch(query)
        }

        // 2. Open YouTube directly
        if (t == "youtube" || t == "open youtube" || t == "launch youtube" || t == "start youtube" || "youtube" in t) {
            return VoiceAction.OpenYouTube
        }

        // 3. Open Terminal directly
        if (Regex("""\b(terminal|konsole|shell|command line)\b""").containsMatchIn(t)) {
            return VoiceAction.OpenTerminal
        }

        // 4. Browser Searches with explicit browser name
        // Pattern A: "search <query> in/on/using/with <browser>"
        Regex("""(?:search|look up|google)\s+(?:for\s+)?(.+?)\s+(?:in|on|using|with)\s+$browserPattern""").find(t)?.let { match ->
            val query = match.groupValues.getOrNull(1)?.trim().orEmpty()
            val browser = match.groupValues.getOrNull(2)?.trim().orEmpty()
            if (query.isNotEmpty() && browser.isNotEmpty()) return VoiceAction.BrowserSearch(query, browser)
        }

        // Pattern B: "search in/on/using/with <browser> (for) <query>"
        Regex("""(?:search|look up|google)\s+(?:in|on|using|with)\s+$browserPattern\s+(?:for\s+)?(.+)""").find(t)?.let { match ->
            val browser = match.groupValues.getOrNull(1)?.trim().orEmpty()
            val query = match.groupValues.getOrNull(2)?.trim().orEmpty()
            if (query.isNotEmpty() && browser.isNotEmpty()) return VoiceAction.BrowserSearch(query, browser)
        }

        // Pattern C: "<browser> search (for) <query>"
        Regex("""$browserPattern\s+(?:search|look up)\s+(?:for\s+)?(.+)""").find(t)?.let { match ->
            val browser = match.groupValues.getOrNull(1)?.trim().orEmpty()
            val query = match.groupValues.getOrNull(2)?.trim().orEmpty()
            if (query.isNotEmpty() && browser.isNotEmpty()) return VoiceAction.BrowserSearch(query, browser)
        }

        // Pattern D: "open <browser> and search (for) <query>"
        Regex("""open\s+$browserPattern\s+and\s+search\s+(?:for\s+)?(.+)""").find(t)?.let { match ->
            val browser = match.groupValues.getOrNull(1)?.trim().orEmpty()
            val query = match.groupValues.getOrNull(2)?.trim().orEmpty()
            if (query.isNotEmpty() && browser.isNotEmpty()) return VoiceAction.BrowserSearch(query, browser)
        }

        // 5. General Web Search (e.g. "search quantum computing", "google recipe for pizza")
        Regex("""^(?:search|google|look up)\s+(?:for\s+)?(.+)""").find(t)?.let { match ->
            val query = match.groupValues.getOrNull(1)?.trim().orEmpty()
            if (query.isNotEmpty() && !query.startsWith("openclaw")) {
                return VoiceAction.BrowserSearch(query, "browser")
            }
        }

        // 6. General Open App ("open <app>", "launch <app>", "start <app>")
        val openAppRegex = Regex("""^(?:open|launch|start|run)\s+(.+)""")
        openAppRegex.find(t)?.let { match ->
            val app = match.groupValues.getOrNull(1)?.trim().orEmpty()
            if (app.isNotEmpty() && app != "openclaw") {
                return VoiceAction.OpenApp(app)
            }
        }

        // 7. Default: OpenClaw
        return VoiceAction.OpenClaw(raw)
    }

    fun executeVoiceCommand(rawCommand: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val action = parseVoiceAction(rawCommand)) {
                is VoiceAction.YouTubeSearch -> {
                    val encodedQuery = URLEncoder.encode(action.query, "UTF-8")
                    controller.openUrl("https://www.youtube.com/results?search_query=$encodedQuery")
                }
                is VoiceAction.OpenYouTube -> {
                    controller.openUrl("https://www.youtube.com")
                }
                is VoiceAction.OpenTerminal -> {
                    controller.openTerminal()
                    delay(400)
                    controller.openRunDialog()
                    delay(400)
                    controller.typeText("konsole")
                    controller.select()
                }
                is VoiceAction.BrowserSearch -> {
                    val encodedQuery = URLEncoder.encode(action.query, "UTF-8")
                    val searchUrl = "https://www.google.com/search?q=$encodedQuery"
                    if (action.browser == "browser" || action.browser == "default") {
                        controller.openUrl(searchUrl)
                    } else {
                        val cmd = when (action.browser) {
                            "chrome" -> "google-chrome"
                            "firefox" -> "firefox"
                            "brave" -> "brave-browser"
                            "edge" -> "microsoft-edge"
                            "opera" -> "opera"
                            "chromium" -> "chromium"
                            else -> action.browser
                        }
                        controller.openRunDialog()
                        delay(500)
                        controller.typeText("$cmd \"$searchUrl\"")
                        delay(500)
                        controller.select()
                    }
                }
                is VoiceAction.OpenApp -> {
                    controller.openRunDialog()
                    delay(500)
                    controller.typeText(action.appName)
                    delay(500)
                    controller.select()
                }
                is VoiceAction.OpenClaw -> {
                    // 1. Open Terminal
                    controller.openTerminal()
                    delay(400)
                    controller.openRunDialog()
                    delay(400)
                    controller.typeText("konsole")
                    controller.select()
                    delay(1000) // Wait for terminal window to launch and gain focus

                    // 2. Launch OpenClaw
                    controller.typeText("openclaw")
                    controller.select()
                    delay(800)

                    // 3. Send spoken user command
                    controller.typeText(action.rawCommand)
                    controller.select()

                    // 4. Send 2nd enter after slight delay
                    delay(400)
                    controller.select()
                }
            }
        }
    }

    fun executeOpenClawVoiceCommand(command: String) {
        executeVoiceCommand(command)
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
