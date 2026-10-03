# KDE Remote - Custom Smart Remote App for KDE Bigscreen & Linux

**KDE Remote** is a redesigned, premium Smart Remote application built on top of the open-source **KDE Connect** Android framework. Developed and optimized by **Anurag Kumar**, it provides a sleek, physical remote interface tailored for controlling PC desktops, KDE Bigscreen TVs, and Linux media setups.

---

## 🌟 Key Features & Optimizations

- **Physical Smart Remote Interface (Claymorphism)**:
  - Designed using Jetpack Compose with tactile, soft 3D claymorphic depth.
  - Supports both **Icy Light** and **Deep Charcoal Dark** themes with instant runtime switching.

- **Choreographer-Driven Trackpad Smoothing**:
  - Eliminates pointer lag, network jitter, and multi-finger jumpiness on Linux.
  - Smoothly buffers and drains pointer deltas synced to the display's exact frame rate (`Choreographer.FrameCallback`).

- **Automated Sudo Shutdown Sequence**:
  - One-tap shutdown button that automatically launches a terminal, executes `sudo shutdown now`, and submits your saved `sudo` password seamlessly.

- **Continuous Auto-Repeat on Hold**:
  - D-Pad directional controls (Up, Down, Left, Right), Center OK button, and Volume (+/-) controls automatically repeat input commands when held down.

- **Real-Time Clipboard Sync & One-Tap Paste**:
  - Automatically syncs the Android phone's clipboard to the PC whenever copied.
  - The dedicated **V** button auto-syncs the latest clipboard text and sends `Ctrl+V` (Paste) in a single press.
  - Includes quick shortcut buttons for **A** (`Ctrl+A`), **C** (`Ctrl+C`), and **V** (`Ctrl+V`).

- **System Audio & Media Controls**:
  - Features dedicated **Play / Pause** media control and **Mute** toggles powered by KDE Connect's `MprisPlugin` and `SystemVolumePlugin`.

- **Underlying KDE Connect Security**:
  - Uses original KDE Connect device discovery, secure TLS pairing, and communication protocols underneath without replacing or duplicating the network layer.

---

## 📱 Developer

Designed and developed by **Anurag Kumar**.
- LinkedIn: [Anurag Kumar](https://www.linkedin.com/in/anurag-kumar-47271335a/?isSelfProfile=true)
- GitHub: [Isksks](https://github.com/Isksks)

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin & Java
- **UI Framework**: Jetpack Compose & Material 3
- **Architecture**: MVVM with Clean Controller abstraction (`RemoteController` → `KdeConnect` Plugins)
- **Min SDK**: 23 (Android 6.0)
- **Compile SDK**: 37

---

## 📄 License

Based on the KDE Connect Android codebase.
Licensed under [GNU GPL v2](https://www.gnu.org/licenses/gpl-2.0.html) and [GNU GPL v3](https://www.gnu.org/licenses/gpl-3.0.html).
