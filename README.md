# Smart Code Screenshots

![Build](https://github.com/anton-erofeev/smart-code-screenshots-intellij-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/com.github.antonerofeev.smartcodescreenshots)](https://plugins.jetbrains.com/plugin/28390-smart-code-screenshots)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/com.github.antonerofeev.smartcodescreenshots.svg)](https://plugins.jetbrains.com/plugin/28390-smart-code-screenshots)

<!-- Plugin description -->
Create polished PNG screenshots of selected code directly in IntelliJ IDEA, with syntax highlighting, a customizable frame, and an optional watermark.

## Features
- Capture selected code with its editor syntax highlighting.
- Copy the screenshot to the clipboard or save it as a PNG.
- Preview the image before saving; zoom, pan, or fit it to the preview window.
- Customize image scale, padding, background color, corner roundness, filename, and window controls.
- Add an optional horizontal or diagonal watermark.

## How to Use
1. Select code in the editor.
2. Choose **Screenshot Selected Code** from the editor context menu, or press <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>S</kbd> (<kbd>⌘</kbd>+<kbd>Shift</kbd>+<kbd>S</kbd> on macOS). You can change the shortcut in **Settings → Keymap**.
3. The screenshot is copied to the clipboard. Use the notification actions to open **Show Preview** or **Save to File**.

In the preview, use **+** and **−** to zoom, **Fit** to fit the image to the window, drag to pan, or hold <kbd>Ctrl</kbd> while scrolling to zoom around the pointer.
<!-- Plugin description end -->

## Installation
- **From Marketplace:** In the IDE, open **Settings → Plugins**, search for **Smart Code Screenshots**, and install it.
- **From disk:** Download the plugin ZIP from the [Releases](https://github.com/anton-erofeev/smart-code-screenshots-intellij-plugin/releases) page and choose **Settings → Plugins → ⚙ → Install Plugin from Disk...**.
- **Build from source:** Clone this repository and run `.\gradlew.bat buildPlugin` on Windows or `./gradlew buildPlugin` on macOS/Linux. Install the generated ZIP from `build/distributions` using **Install Plugin from Disk...**.

## Requirements
- IntelliJ-based IDE version 2023.2 or newer (build 232+).
- JDK 21 to build the plugin. No separate JDK installation is required to use it in the IDE.

## Settings
Open **Settings → Tools → Smart Code Screenshots** to adjust the screenshot appearance and watermark.

## Feedback & Contributions
- Report bugs and suggest features via [Issues](https://github.com/anton-erofeev/smart-code-screenshots-intellij-plugin/issues)
- Pull requests are welcome!
