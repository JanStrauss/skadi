#Skadi v2
Skadi is a lightweight tool that allows you to comfortably watch Twitch channels via VLC and livestreamer and to ~~enjoy~~ open the Twitch chat of the channels you are watching. Skadi can import the channels you follow from Twitch so you don't have to add them all manually.

v2 is a rewrite using JavaFX and Java 8 features.

![skadi screenshot 1](https://i.imgur.com/YBkW44y.png "main window")
![skadi screenshot 2](https://i.imgur.com/FTz7lUd.png "channel details")
![skadi screenshot 3](https://i.imgur.com/ExnJCtW.png "open stream and chat")

## Download
see https://github.com/s1mpl3x/skadi/releases

## Required software
You need to have [Java 1.8u40+](https://www.java.com/download/) installed, as well as [Chrome](https://www.google.com/chrome/) (or chromium), [VLC](https://www.videolan.org/vlc/) and [livestreamer](https://github.com/chrippa/livestreamer/releases).
Make sure to keep livestreamer up to date.

## Features
* version check / update download
* import followed channels
* ~~channel filtering~~ (disabled due to javafx runtime bug: https://javafx-jira.kenai.com/browse/RT-39710 , to be fixed in 8u60)
* streams can be opened in all available stream qualities
* chats are opened in chrome
* channel detail pane (double click on a channel or click the 'i' button) showing the channel panels, preview, stats and a viewer diagram
* logging
* notifications if a channel goes live
* minimize to tray (https://javafx-jira.kenai.com/browse/RT-17503 uses old AWT API)
* drag and drop channel names/urls do add
* channel auto updated every 60s / force refresh

### KNOWN BUGS
* minimize to tray bugged on xfce desktop
* window position on windows is reset after opening from tray when using multiple screens (https://javafx-jira.kenai.com/browse/RT-38952)

### TODO/PLANNED
see https://github.com/s1mpl3x/skadi/issues

## Setup
If Skadi fails to open streams or chats with the default configuration values (see the logs), you might need to change the paths for Chrome, VLC and livestreamer in the settings dialog. 
The log and config are stored under `{user.home}/.skadi/`.

## Usage
launch Skadi via `java -jar skadi.jar` if a double click on the jar does not work.

## Building
Skadi uses maven as build tool.
use `mvn package` to build Skadi, result can be found at `target/Skadi-xyz.jar`
