# Smart Code Screenshots IntelliJ Plugin

![Build](https://github.com/anton-erofeev/smart-code-screenshots-intellij-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/com.github.antonerofeev.smartcodescreenshots)](https://plugins.jetbrains.com/plugin/28390-smart-code-screenshots)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/com.github.antonerofeev.smartcodescreenshots.svg)](https://plugins.jetbrains.com/plugin/28390-smart-code-screenshots)

<!-- Plugin description -->
Smart Code Screenshots is a plugin for IntelliJ IDEA that lets you quickly create beautiful screenshots of selected code directly from the editor.

## Features
- Create screenshots of selected code with syntax highlighting and formatting
- Copy screenshots to clipboard with a single command
- Save screenshots as PNG files
- Quick access via editor context menu and keyboard shortcut

## How to Use
1. Select the code fragment you want in the editor.
2. Right-click and choose **Screenshot Selected Code** or use the shortcut <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>S</kbd>.
3. The screenshot will be copied to your clipboard. You can also save it as a file via the notification popup.
<!-- Plugin description end -->

## Installation
- From Marketplace: search for "Smart Code Screenshots" in IntelliJ IDEA plugins.
- Or build manually:
  1. Clone the repository
  2. Run `./gradlew build`
  3. Install the JAR from `build/libs` via **Settings → Plugins → Install plugin from disk...**

## Requirements
- IntelliJ IDEA 2022.3 or newer
- JDK 17+

## Feedback & Contributions
- Report bugs and suggest features via [Issues](https://github.com/antonerofeev/smart-code-screenshots-intellij-plugin/issues)
- Pull requests are welcome!

---
Author: [anton-erofeev](https://github.com/antonerofeev)
