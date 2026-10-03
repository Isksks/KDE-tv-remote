package org.kde.kdeconnect.ui.remote

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import org.kde.kdeconnect.BackgroundService
import org.kde.kdeconnect.plugins.share.SendFileActivity
import org.kde.kdeconnect.ui.ThemeUtil
import org.kde.kdeconnect.ui.remote.controller.RemoteViewModel
import org.kde.kdeconnect.ui.remote.settings.SettingsScreen
import org.kde.kdeconnect.ui.remote.theme.RemoteTheme

class RemoteActivity : ComponentActivity() {
    
    private val viewModel: RemoteViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        BackgroundService.Start(applicationContext)
    }

    override fun onResume() {
        super.onResume()
        viewModel.syncClipboard()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.setUserPreferredTheme(application)
        
        setContent {
            val prefs = remember { PreferenceManager.getDefaultSharedPreferences(this@RemoteActivity) }
            var themePref by remember {
                mutableStateOf(prefs.getString("theme_pref", ThemeUtil.DEFAULT_MODE) ?: ThemeUtil.DEFAULT_MODE)
            }
            val systemDark = isSystemInDarkTheme()
            
            val isDark = when (themePref) {
                ThemeUtil.DARK_MODE -> true
                ThemeUtil.LIGHT_MODE -> false
                else -> systemDark
            }

            RemoteTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RemoteTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(navController = navController, startDestination = "remote") {
                        composable("remote") {
                            RemoteScreen(
                                viewModel = viewModel,
                                onNavigateToSettings = { navController.navigate("settings") }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToSendFile = {
                                    val deviceId = viewModel.getDeviceId()
                                    if (deviceId != null) {
                                        val intent = Intent(this@RemoteActivity, SendFileActivity::class.java)
                                        intent.putExtra("deviceId", deviceId)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(this@RemoteActivity, "No device connected", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                isDarkMode = isDark,
                                onDarkModeChanged = { enabled ->
                                    val nextTheme = if (enabled) ThemeUtil.DARK_MODE else ThemeUtil.LIGHT_MODE
                                    prefs.edit().putString("theme_pref", nextTheme).apply()
                                    ThemeUtil.applyTheme(nextTheme)
                                    themePref = nextTheme
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
