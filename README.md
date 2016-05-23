#Skadi v2
Skadi allows you to comfortably watch Twitch channels via livestreamer / VLC (or any videoplayer compatible with livestreamer) and to ~~enjoy~~ open the Twitch chat of the channels you are watching. Skadi can import the channels you follow from Twitch so you don't have to add them all manually and will display a notification if a channel goes live.

v2 is a rewrite using JavaFX and Java 8 features.

![skadi screenshot 1](https://i.imgur.com/cQps54F.jpg "grid view dark theme")
![skadi screenshot 2](https://i.imgur.com/RpWgRoA.jpg "grid view light theme")
![skadi screenshot 3](https://i.imgur.com/nUFr3Wa.jpg "channel detail pane")
![skadi screenshot 5](https://i.imgur.com/ExnJCtW.png "open stream and chat")

## Download
see https://github.com/s1mpl3x/skadi/releases

## Required software
* [Java 1.8u60+](https://www.java.com/download/) 
* [livestreamer](https://github.com/chrippa/livestreamer/releases)
* [Chrome](https://www.google.com/chrome/) (or chromium)
* [VLC](https://www.videolan.org/vlc/) (or any videoplayer compatible with livestreamer, see  [livestreamer documentation](http://docs.livestreamer.io/players.html))

Make sure to keep livestreamer and Java up to date.

## Features
* version check / update download
* import followed channels
* channel filtering
* streams can be opened in all available stream qualities
* chats are opened in chrome/chromium
* channel detail pane (double click on a channel or click the 'i' button) showing the channel panels, preview, stats, sub-emotes and a viewer graph
* light and dark theme
* table and grid view
* logging
* notifications if a channel goes live
* minimize to tray (https://javafx-jira.kenai.com/browse/RT-17503 uses old AWT API)
* drag and drop channel names/urls do add
* channel auto updated every 60s / force refresh
* twitch-auth and followed sync between Skadi and Twitch

### TODO/PLANNED/KNOWN BUGS
see https://github.com/s1mpl3x/skadi/issues 
If you have a feature request or found a bug, create a new issue (and include the log file if appropriate). 

## Setup
If Skadi fails to open streams or chats with the default configuration values (see log file or the status bar), you might need to change the paths for Chrome or livestreamer in the settings dialog. Often the problem isn't related to Skadi but to livestreamer, so you might want to check if livestreamer is setup correctly first.
The log and config are stored under `{user.home}/.skadi/` and in the settings dialog there is a button to open the log.

## Usage
launch Skadi via `java -jar skadi.jar` if a double click on the jar does not work.

## Building
Skadi uses maven as build tool.
use `mvn package` to build Skadi, result can be found at `target/Skadi-xyz.jar`
