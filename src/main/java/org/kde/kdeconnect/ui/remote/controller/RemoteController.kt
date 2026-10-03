package org.kde.kdeconnect.ui.remote.controller

import android.view.Choreographer
import org.kde.kdeconnect.KdeConnect
import org.kde.kdeconnect.plugins.clipboard.ClipboardPlugin
import org.kde.kdeconnect.plugins.mousepad.MousePadPlugin
import org.kde.kdeconnect.plugins.mpris.MprisPlugin
import org.kde.kdeconnect.plugins.systemvolume.SystemVolumePlugin
import kotlin.math.abs

class RemoteController {
    
    private var currentDeviceId: String? = null
    
    fun setDeviceId(deviceId: String?) {
        this.currentDeviceId = deviceId
    }
    
    fun getDeviceId(): String? = currentDeviceId

    private fun getMousePadPlugin(): MousePadPlugin? {
        val id = currentDeviceId ?: return null
        return KdeConnect.getInstance().getDevicePlugin(id, MousePadPlugin::class.java)
    }

    private fun getSystemVolumePlugin(): SystemVolumePlugin? {
        val id = currentDeviceId ?: return null
        return KdeConnect.getInstance().getDevicePlugin(id, SystemVolumePlugin::class.java)
    }
    
    private fun getMprisPlugin(): MprisPlugin? {
        val id = currentDeviceId ?: return null
        return KdeConnect.getInstance().getDevicePlugin(id, MprisPlugin::class.java)
    }

    fun playPause() {
        val plugin = getMprisPlugin()
        val player = plugin?.playingPlayer ?: plugin?.playerList?.firstOrNull()?.let { plugin.getPlayerStatus(it) }
        player?.sendPlayPause()
    }

    // Frame-buffered mouse movement smoothing
    private var bufferX = 0f
    private var bufferY = 0f
    private var drainScheduled = false

    private val drainCallback = Choreographer.FrameCallback {
        drainScheduled = false
        drainMouseBuffer()
    }

    private fun queueMouseDelta(dx: Float, dy: Float) {
        bufferX += dx
        bufferY += dy
        if (!drainScheduled) {
            drainScheduled = true
            Choreographer.getInstance().postFrameCallback(drainCallback)
        }
    }

    private fun drainMouseBuffer() {
        val sx: Float
        val sy: Float
        if (abs(bufferX) < 0.5f && abs(bufferY) < 0.5f) {
            sx = bufferX
            sy = bufferY
        } else {
            sx = bufferX * 0.5f
            sy = bufferY * 0.5f
        }
        bufferX -= sx
        bufferY -= sy

        if (sx != 0f || sy != 0f) {
            getMousePadPlugin()?.sendMouseDelta(sx, sy)
        }

        if (bufferX != 0f || bufferY != 0f) {
            drainScheduled = true
            Choreographer.getInstance().postFrameCallback(drainCallback)
        }
    }

    fun moveMouse(dx: Float, dy: Float) {
        queueMouseDelta(dx * 1.5f, dy * 1.5f)
    }

    fun leftClick() {
        getMousePadPlugin()?.sendLeftClick()
    }

    fun rightClick() {
        getMousePadPlugin()?.sendRightClick()
    }

    fun doubleClick() {
        getMousePadPlugin()?.sendDoubleClick()
    }

    fun scroll(amount: Float) {
        // Send scroll to mousepad
        getMousePadPlugin()?.sendScroll(0.0, amount.toDouble())
    }

    fun typeText(text: String) {
        getMousePadPlugin()?.sendText(text)
    }
    
    fun backspace() {
        getMousePadPlugin()?.sendBackspace()
    }
    
    fun syncClipboard() {
        val id = getDeviceId() ?: return
        val plugin = KdeConnect.getInstance().getDevicePlugin(id, ClipboardPlugin::class.java)
        plugin?.performSilentClipboardSync()
    }

    fun ctrlA() {
        getMousePadPlugin()?.sendCtrlA()
    }

    fun ctrlC() {
        getMousePadPlugin()?.sendCtrlC()
    }

    fun ctrlV() {
        syncClipboard()
        getMousePadPlugin()?.sendCtrlV()
    }

    fun up() {
        getMousePadPlugin()?.sendUp()
    }

    fun down() {
        getMousePadPlugin()?.sendDown()
    }

    fun left() {
        getMousePadPlugin()?.sendLeft()
    }

    fun right() {
        getMousePadPlugin()?.sendRight()
    }

    fun select() {
        getMousePadPlugin()?.sendSelect()
    }

    fun home() {
        getMousePadPlugin()?.sendHome()
    }

    fun back() {
        getMousePadPlugin()?.sendClose()
    }
    
    fun openTerminal() {
        getMousePadPlugin()?.sendCtrlAltT()
    }

    fun openRunDialog() {
        getMousePadPlugin()?.sendAltF2()
    }
    
    fun power() {
        val plugin = getMousePadPlugin()
        plugin?.sendText("sudo shutdown now")
        plugin?.sendSelect()
    }

    fun volumeUp() {
        val plugin = getSystemVolumePlugin()
        val defaultSink = plugin?.sinks?.firstOrNull { it.isDefault } ?: plugin?.sinks?.firstOrNull()
        defaultSink?.let { sink ->
            val step = sink.maxVolume * 5 / 100
            plugin?.sendVolume(sink.name, (sink.volume + step).coerceIn(0, sink.maxVolume))
        }
    }

    fun volumeDown() {
        val plugin = getSystemVolumePlugin()
        val defaultSink = plugin?.sinks?.firstOrNull { it.isDefault } ?: plugin?.sinks?.firstOrNull()
        defaultSink?.let { sink ->
            val step = sink.maxVolume * 5 / 100
            plugin?.sendVolume(sink.name, (sink.volume - step).coerceIn(0, sink.maxVolume))
        }
    }

    fun mute() {
        val plugin = getSystemVolumePlugin()
        val defaultSink = plugin?.sinks?.firstOrNull { it.isDefault } ?: plugin?.sinks?.firstOrNull()
        if (defaultSink != null) {
            plugin?.sendMute(defaultSink.name, !defaultSink.isMute())
        } else {
            plugin?.sinks?.forEach { sink ->
                plugin.sendMute(sink.name, !sink.isMute())
            }
        }
    }
}
