/*
 * SPDX-FileCopyrightText: 2014 Albert Vaca Cintora <albertvaka@gmail.com>
 *
 * SPDX-License-Identifier: GPL-2.0-only OR GPL-3.0-only OR LicenseRef-KDE-Accepted-GPL
*/
package org.kde.kdeconnect

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.MainThread
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.kde.kdeconnect.backends.BaseLinkProvider
import org.kde.kdeconnect.backends.BaseLinkProvider.ConnectionReceiver
import org.kde.kdeconnect.backends.bluetooth.BluetoothLinkProvider
import org.kde.kdeconnect.backends.lan.LanLinkProvider
import org.kde.kdeconnect.helpers.NotificationHelper
import org.kde.kdeconnect.plugins.clipboard.ClipboardFloatingActivity
import org.kde.kdeconnect.plugins.clipboard.ClipboardPlugin
import org.kde.kdeconnect.plugins.runcommand.RunCommandActivity
import org.kde.kdeconnect.plugins.runcommand.RunCommandPlugin
import org.kde.kdeconnect.plugins.share.SendFileActivity
import org.kde.kdeconnect.ui.MainActivity
import org.kde.kdeconnect.ui.remote.RemoteActivity
import org.kde.kdeconnect_tp.R

/**
 * This class (still) does 3 things:
 * - Keeps the app running by creating a foreground notification.
 * - Holds references to the active LinkProviders, but doesn't handle the DeviceLink those create (the KdeConnect class does that).
 * - Listens for network connectivity changes and tells the LinkProviders to re-check for devices.
 * It can be started by the KdeConnectBroadcastReceiver on some events or when the MainActivity is launched.
 */
class BackgroundService : Service() {
    private lateinit var applicationInstance: KdeConnect

    private val linkProviders = mutableListOf<BaseLinkProvider>()

    /** Indicates whether device is connected over wifi / usb / bluetooth / (anything other than cellular) */
    val isConnectedToNonCellularNetwork: LiveData<Boolean>
        field = MutableLiveData<Boolean>()

    fun updateForegroundNotification() {
        val notificationManager = getSystemService<NotificationManager>() ?: return

        val connectedDevices = mutableListOf<String>()
        val connectedDeviceIds = mutableListOf<String>()
        if (NotificationHelper.isPersistentNotificationEnabled(this)) {
            for (device in applicationInstance.devices.values) {
                if (device.isReachable && device.isPaired) {
                    connectedDeviceIds.add(device.deviceId)
                    connectedDevices.add(device.name)
                }
            }
        }

        val notification = createForegroundNotification(connectedDevices, connectedDeviceIds)
        startForegroundServiceNotification(notificationManager, notification)

        if (connectedDevices.isEmpty()) {
            // Remove / hide notification when no devices are connected
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
            notificationManager.cancel(FOREGROUND_NOTIFICATION_ID)
        }
    }

    private fun startForegroundServiceNotification(
        notificationManager: NotificationManager,
        notification: Notification
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                startForeground(FOREGROUND_NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE)
            } catch (e: Exception) {
                try {
                    startForeground(FOREGROUND_NOTIFICATION_ID, notification)
                } catch (e2: Exception) {
                    notificationManager.notify(FOREGROUND_NOTIFICATION_ID, notification)
                }
            }
        } else {
            startForeground(FOREGROUND_NOTIFICATION_ID, notification)
        }
    }

    private fun registerLinkProviders() {
        linkProviders.add(LanLinkProvider(this))
        //linkProviders.add(LoopbackLinkProvider(this))
        linkProviders.add(BluetoothLinkProvider(this))
    }

    fun onNetworkChange(network: Network?) {
        if (!initialized) {
            Log.d(LOG_TAG, "ignoring onNetworkChange called before the service is initialized")
            return
        }
        Log.d(LOG_TAG, "onNetworkChange")
        for (linkProvider in linkProviders) {
            linkProvider.onNetworkChange(network)
        }
    }

    fun addConnectionListener(connectionReceiver: ConnectionReceiver) {
        for (linkProvider in linkProviders) {
            linkProvider.addConnectionReceiver(connectionReceiver)
        }
    }

    fun removeConnectionListener(connectionReceiver: ConnectionReceiver) {
        for (linkProvider in linkProviders) {
            linkProvider.removeConnectionReceiver(connectionReceiver)
        }
    }

    /** This will called only once, even if we launch the service intent several times */
    @MainThread
    override fun onCreate() {
        super.onCreate()
        Log.d("KdeConnect/BgService", "onCreate")
        this.applicationInstance = KdeConnect.getInstance()
        instance = this

        KdeConnect.getInstance().addDeviceListChangedCallback("BackgroundService", this::updateForegroundNotification)

        // Register screen on listener
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        // See: https://developer.android.com/reference/android/net/ConnectivityManager.html#CONNECTIVITY_ACTION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        }
        registerReceiver(KdeConnectBroadcastReceiver(), filter)

        // Watch for changes on all network connections except cellular networks
        val networkRequestBuilder = createNonCellularNetworkRequestBuilder()
        val connectivityManager = this.getSystemService<ConnectivityManager>()
        connectivityManager?.registerNetworkCallback(networkRequestBuilder.build(), object : NetworkCallback() {

            // All callbacks run on a dedicated thread that isn't the main thread

            override fun onAvailable(network: Network) {
                Log.i("BackgroundService", "Valid network available")
                isConnectedToNonCellularNetwork.postValue(true)
                onNetworkChange(network)
            }

            override fun onLost(network: Network) {
                Log.i("BackgroundService", "Valid network lost")
                isConnectedToNonCellularNetwork.postValue(false)
                onNetworkChange(network)
            }
        })

        registerLinkProviders()
        addConnectionListener(applicationInstance.connectionListener) // Link Providers need to be already registered
        for (linkProvider in linkProviders) {
            linkProvider.onStart()
        }
        initialized = true
    }

    fun changePersistentNotificationVisibility(visible: Boolean) {
        if (visible) {
            updateForegroundNotification()
        }
        else {
            stopForeground(true)
            Start(this)
        }
    }

    private fun createForegroundNotification(
        connectedDevices: List<String>,
        connectedDeviceIds: List<String>
    ): Notification {
        // Launch RemoteActivity (new Remote UI)
        val intent = Intent(this, RemoteActivity::class.java)
        if (connectedDeviceIds.size == 1) {
            intent.putExtra("deviceId", connectedDeviceIds[0])
        }

        val pi = PendingIntent.getActivity(this, 0, intent, UPDATE_IMMUTABLE_FLAGS)
        val notification = NotificationCompat.Builder(this, NotificationHelper.Channels.PERSISTENT).apply {
            setSmallIcon(R.drawable.ic_notification)
            setOngoing(true)
            setContentIntent(pi)
            setPriority(NotificationCompat.PRIORITY_MIN)
            setShowWhen(false)
            setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            setAutoCancel(false)
            setGroup("BackgroundService")
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notification.setContentTitle(getString(R.string.kde_connect))
        }

        notification.setContentText(
            getString(R.string.foreground_notification_devices, connectedDevices.joinToString(", "))
        )

        // Removed copy clipboard and file share buttons per request

        if (connectedDeviceIds.size == 1) {
            val deviceId = connectedDeviceIds[0]
            val device = KdeConnect.getInstance().getDevice(deviceId)
            if (device != null) {
                val plugin = device.getPlugin("RunCommandPlugin") as RunCommandPlugin?
                if (plugin != null && plugin.commandList.isNotEmpty()) {
                    val runCommand = Intent(this, RunCommandActivity::class.java)
                    runCommand.putExtra("deviceId", connectedDeviceIds[0])
                    val runPendingCommand = PendingIntent.getActivity(this, 2, runCommand, UPDATE_IMMUTABLE_FLAGS)
                    notification.addAction(0, getString(R.string.pref_plugin_runcommand), runPendingCommand)
                }
            }
        }
        return notification.build()
    }

    override fun onDestroy() {
        Log.d("KdeConnect/BgService", "onDestroy")
        initialized = false
        for (linkProvider in linkProviders) {
            linkProvider.onStop()
        }
        KdeConnect.getInstance().removeDeviceListChangedCallback("BackgroundService")
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "onStartCommand")
        updateForegroundNotification()
        if (intent != null && intent.getBooleanExtra("refresh", false)) {
            onNetworkChange(null)
        }
        return START_STICKY
    }

    companion object {
        const val LOG_TAG = "KDE/BackgroundService"

        const val UPDATE_IMMUTABLE_FLAGS = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        private const val FOREGROUND_NOTIFICATION_ID = 1

        @JvmStatic
        var instance: BackgroundService? = null
            private set

        private var initialized = false

        private fun createNonCellularNetworkRequestBuilder(): NetworkRequest.Builder {
            return NetworkRequest.Builder().apply {
                // NetworkRequest.Builder requires NET_CAPABILITY_NOT_VPN by default.
                // Remove it so this request can match VPN networks as well.
                removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
                addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                addTransportType(NetworkCapabilities.TRANSPORT_VPN)
                addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    addTransportType(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                    addTransportType(NetworkCapabilities.TRANSPORT_USB)
                    addTransportType(NetworkCapabilities.TRANSPORT_LOWPAN)
                }
            }
        }

        fun Start(context: Context) {
            Log.d(LOG_TAG, "Start")
            val intent = Intent(context, BackgroundService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        @JvmStatic
        fun ForceRefreshConnections(context: Context) {
            Log.d(LOG_TAG, "ForceRefreshConnections")
            val intent = Intent(context, BackgroundService::class.java)
            intent.putExtra("refresh", true)
            ContextCompat.startForegroundService(context, intent)
        }
    }
}
