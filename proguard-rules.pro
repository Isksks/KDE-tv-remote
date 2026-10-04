# Add project specific ProGuard / R8 rules here:

-keepattributes SourceFile,LineNumberTable,Signature,*Annotation*

# Keep Loadable Plugins for reflection
-keep @org.kde.kdeconnect.plugins.PluginFactory$LoadablePlugin class * { *; }
-keepclassmembers class * {
    @org.kde.kdeconnect.plugins.PluginFactory$LoadablePlugin *;
}

# Keep core components
-keep class org.kde.kdeconnect.KdeConnect { *; }
-keep class org.kde.kdeconnect.BackgroundService { *; }
-keep class org.kde.kdeconnect.NetworkPacket { *; }
-keep class org.kde.kdeconnect.Device { *; }
-keep class org.kde.kdeconnect.plugins.Plugin { *; }
-keep class org.kde.kdeconnect.ui.remote.** { *; }

# SSHd requires mina, and mina uses reflection so some classes would get deleted
-keep class org.apache.sshd.** { *; }
-dontwarn org.apache.sshd.**

# The android-smsmms library uses class casting and reflection internally.
-keep class com.google.android.mms.** { *; }
