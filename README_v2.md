#Skadi v2
Skadi is a lightweight tool that allows you to comfortably watch Twitch channels via VLC and livestreamer and to ~~enjoy~~ open the Twitch chat of the channels you are watching. Skadi can import the channels you follow from Twitch so you don't have to add them all manually.

v2 is a rewrite of the old Swing based Skadi using JavaFX.

![skadi screenshot 1](https://i.imgur.com/sjdQQs4.png "main window")
![skadi screenshot 2](https://i.imgur.com/ExnJCtW.png "open stream and chat")

## Download

see https://github.com/s1mpl3x/skadi/releases

## Feature progress

### DONE
* single instance
* version check
* channel import
* channel filtering
* chat/stream handling
* io
* channel detail pane
* improve chat/stream handling (chat process != chat window, can't track/close chat windows)
* logging
* async panel retrieval
* notifications
* settings dialog
* minimize to tray (https://javafx-jira.kenai.com/browse/RT-17503 have to use old awt api)(https://javafx-jira.kenai.com/browse/RT-38952 screen switching on windows)
* stream quality retrieval

### TODO
* improve detail window

### KNOWN BUGS
* affected by javafx runtime bug: https://javafx-jira.kenai.com/browse/RT-39710

### PLANNED
* installer
* updater
* better chat handling
* use twitch oauth(?)

## Required software
You need to have [Java 1.8u40+](https://www.java.com/download/) installed, as well as [Chrome](https://www.google.com/chrome/), [VLC](https://www.videolan.org/vlc/) and [livestreamer](https://github.com/chrippa/livestreamer/releases).

## Setup
If Skadi fails to open streams or chats with the default configuration values (see the logs), you might need to change the paths for Chrome, VLC and livestreamer in the settings dialog. 
The configuration file and and logs are stored under `{userhome}/.skadi/`.

### Example skadi_state.xml for Windows
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<skadi_data>
    <executable_chrome>C:\Program Files (x86)\Google\Chrome\Application\chrome.exe</executable_chrome>
    <executable_livestreamer>C:\Program Files (x86)\Livestreamer\livestreamer.exe</executable_livestreamer>
    <executable_vlc>C:\Program Files (x86)\VideoLAN\VLC\vlc.exe</executable_vlc>
    <display_notifications>true</display_notifications>
    <minimize_to_tray>true</minimize_to_tray>
    <online_filter_active>false</online_filter_active>
    <channels/>
</skadi_data>

```

### Example skadi_state.xml for linux
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<skadi_data>
    <executable_chrome>chromium-browser</executable_chrome>
    <executable_livestreamer>livestreamer</executable_livestreamer>
    <executable_vlc>vlc</executable_vlc>
    <display_notifications>true</display_notifications>
    <minimize_to_tray>true</minimize_to_tray>
    <online_filter_active>false</online_filter_active>
    <channels/>
</skadi_data>
```

## Usage

launch Skadi via `java -jar skadi.jar`
if a double click on the jar does not work.

After Skadi launched, you should see the UI. You can now add or import channels. If you then select one of your just added channels you can open the stream and/or chat.
