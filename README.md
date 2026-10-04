# 📺 KDE Remote

### A modern Android remote for KDE Plasma Bigscreen & Linux

Turn your Android phone into a **dedicated remote control for your Linux PC, KDE Plasma Bigscreen, and media center**.

Built on top of the open-source **KDE Connect Android** framework, KDE Remote keeps KDE Connect's reliable device discovery and secure communication while providing a completely redesigned, TV-focused remote experience.

---

## ✨ Features

### 🎮 Complete Remote

- 🕹️ D-Pad navigation
- 🖱️ Touchpad / mouse control
- 🔊 Volume Up / Down
- ⏯️ Media playback controls
- 🏠 Home & Back navigation
- ⌨️ Keyboard input
- 🔴 Power / system controls
- 📋 Clipboard support
- ⚡ Quick A / C / V shortcuts

### 🎨 Modern Interface

- Clean TV-remote inspired layout
- Dark & Light modes
- Large touch-friendly controls
- Responsive interface
- Smooth touchpad movement
- Designed for one-handed use

### 🔐 Built on KDE Connect

KDE Remote uses KDE Connect's existing communication infrastructure for:

- Device discovery
- Secure pairing
- Network communication
- Remote input
- Clipboard synchronization

---

# 🔗 Connection

KDE Remote communicates with your Linux computer over your **local Wi-Fi network**.

```text
┌─────────────────┐
│   Android Phone │
│   KDE Remote    │
└────────┬────────┘
         │
         │ Wi-Fi
         ▼
┌─────────────────┐
│  Linux Computer │
│ KDE Connect     │
│ KDE Bigscreen    │
└─────────────────┘
```

> **Important:** Your Android phone and Linux computer should normally be connected to the **same local network**.

---

# 🚀 Getting Started

## 1. Install KDE Connect on Linux

Install **KDE Connect** on your Linux PC.

### Ubuntu / Kubuntu / Debian

```bash
sudo apt install kdeconnect
```

### Fedora

```bash
sudo dnf install kdeconnect
```

### Arch Linux

```bash
sudo pacman -S kdeconnect
```

You can also install KDE Connect using your distribution's software manager.

---

## 2. Install KDE Remote

Download the latest KDE Remote APK and install it on your Android device.

> Android may ask you to allow installation from unknown sources when installing an APK manually.

---

## 3. Connect Both Devices to Wi-Fi

Make sure:

**Android phone → Wi-Fi**

**Linux PC → Same Wi-Fi network**

For example:

```text
📱 Phone
   │
   ├── Wi-Fi ──┐
               │
               ▼
          📡 Router
               │
               └── Wi-Fi / Ethernet
                       │
                       ▼
                 💻 Linux PC
```

Ethernet is also fine for the PC as long as both devices can communicate on the same local network.

---

# 🔐 4. Pair Your Devices

### On Android

1. Open **KDE Remote**
2. Wait for your Linux computer to appear
3. Tap your computer
4. Send a pairing request

### On Linux

A KDE Connect pairing notification should appear.

Select:

**Accept**

Your devices are now paired.

---

# 📺 5. Start Using the Remote

Once pairing is complete:

1. Open **KDE Remote**
2. Select your paired computer
3. The remote interface will open
4. Start controlling your Linux desktop or KDE Bigscreen

No additional remote server is required.

---

# 🎮 Remote Controls

| Control      | Function              |
| ------------ | --------------------- |
| 🕹️ D-Pad    | Navigate menus        |
| 🖱️ Touchpad | Move mouse            |
| 🏠 Home      | Go to home            |
| ↩️ Back      | Navigate back         |
| 🔊 + / −     | Adjust volume         |
| ⏯️ Media     | Play / pause media    |
| ⌨️ Keyboard  | Send keyboard input   |
| A            | Keyboard shortcut A   |
| C            | Copy                  |
| V            | Paste                 |
| 📋 Clipboard | Synchronize clipboard |

---

# 🌑 Dark Mode

KDE Remote includes a dedicated dark interface designed for use in low-light environments.

The interface can be switched between:

**☀️ Light Mode**

and

**🌙 Dark Mode**

---

# 🖥️ KDE Plasma Bigscreen

KDE Remote is especially designed for **KDE Plasma Bigscreen**.

It provides large, easy-to-use controls suitable for navigating a TV-style interface from across the room.

```text
        📱 Android
             │
             │ Wi-Fi
             ▼
      ┌──────────────┐
      │ KDE Connect  │
      └──────┬───────┘
             │
             ▼
      📺 KDE Bigscreen
```

---

# 🛠️ Troubleshooting

### My PC isn't appearing

Check that:

- Both devices are on the same network
- KDE Connect is running on Linux
- KDE Remote has network permission
- Your firewall isn't blocking KDE Connect
- The devices can communicate with each other

Try restarting KDE Connect:

```bash
kdeconnect-cli --refresh
```

Then reopen KDE Remote.

---

### Pairing doesn't work

Make sure KDE Connect is running:

```bash
kdeconnect-cli --list-devices
```

If your device isn't detected, check your firewall/network configuration.

---

### Remote controls aren't working

Open KDE Connect on Linux and make sure the required plugins are enabled.

Then disconnect and reconnect the device.

---

# 🧩 Requirements

### Android

- Android 8.0+
- Wi-Fi connection
- KDE Remote installed

### Linux

- KDE Connect
- Linux desktop / KDE Plasma
- Local network connection

### Recommended

- KDE Plasma Bigscreen
- 5 GHz Wi-Fi
- Linux PC connected through Ethernet

---

# 🏗️ Built With

- **Kotlin**
- **Jetpack Compose**
- **KDE Connect Android**
- **Android SDK**

KDE Remote builds upon the open-source KDE Connect Android project while introducing a dedicated remote-control interface.

---

# 🤝 Contributing

Contributions are welcome!

You can help by:

- 🐛 Reporting bugs
- 💡 Suggesting features
- 🎨 Improving the UI
- 💻 Improving the code
- 🧪 Testing on different Linux distributions
- 📺 Testing with KDE Plasma Bigscreen

### Getting Started

```bash
git clone https://github.com/Isksks/KDE-tv-remote.git
```

Open the project in **Android Studio**, allow Gradle to sync, and build the application.

---

# 🗺️ Roadmap

- [x] Improved device discovery
- [x] Automatic reconnect
- [x] Better Bigscreen navigation
- [x] Customizable remote layouts
- [x] More media controls
- [x] TV application shortcuts
- [x] Improved landscape mode
- [x] More customization options
- [x] Stable release builds

---

# ❤️ Credits

KDE Remote is built using the excellent open-source work of the **KDE Connect** project.

Huge thanks to the KDE community and everyone contributing to the KDE ecosystem.

---

## 📄 License

See the project's license for details.

---



### 📺 Your phone. Your Linux PC. One remote.

**Built for KDE Plasma Bigscreen.**

\</p>
