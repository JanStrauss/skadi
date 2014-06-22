skadi
=====
Skadi is a lightweight toolkit that allows you to comfortably watch twitch.tv channels via VLC and/or livestreamer. It also allows you to directly open the popup-chat of the channels you are watching without having to first open the twitch url and open it up manually. Skadi can import the channels you follow from twitch so you don't have to add them all by hand.

![skadi screenshot](https://i.imgur.com/ZFhXWOQ.png "Skadi screenshot")

## Download

see https://github.com/s1mpl3x/skadi/releases

## Planned features

* minimize to tray
* show message if saved channel goes live
* settings dialog

## Required software
You need to have [Java 1.7+](https://www.java.com/de/download/) installed, as well as [Chrome](https://www.google.com/chrome/) and [VLC](https://www.videolan.org/vlc/). Per default Skadi uses  [livestreamer](https://github.com/chrippa/livestreamer/releases), but if you change the config file (see below) it can also work without it if you don't want to install livestreamer.

## Setup
If skadi fails to open streams with the default config (see the event log at the bottom of the window), you might need to change them in the settings file which you can find at
```
{userhome}/.skadi/skadi_data.xml 
```
You have to provide the paths to the Chrome, VLC and livestreamer executables. With the USE_LIVESTREAMER flag you can turn of the usage of livestreamer if wished, Skadi will then use VLC directly.

### Example skadi_data.xml for Windows
```
<?xml version="1.0" encoding="UTF-8"?>
<SKADI_DATA VERSION="1.0">
   <EXECUTABLES>
      <CHROME>C:\Program Files (x86)\Google\Chrome\Application\chrome.exe</CHROME>
      <LIVESTREAMER>C:\Program Files (x86)\Livestreamer\livestreamer.exe</LIVESTREAMER>
      <VLC>C:\Program Files (x86)\VideoLAN\VLC\vlc.exe</VLC>
   </EXECUTABLES>
   <USE_LIVESTREAMER>true</USE_LIVESTREAMER>
   <CHANNELS />
</SKADI_DATA>
```

### Example skadi_data.xml for linux
```
<?xml version="1.0" encoding="UTF-8"?>
<SKADI_DATA VERSION="1.0">
   <EXECUTABLES>
      <CHROME>chromium-browser</CHROME>
      <LIVESTREAMER>livestreamer</LIVESTREAMER>
      <VLC>vlc</VLC>
   </EXECUTABLES>
   <USE_LIVESTREAMER>true</USE_LIVESTREAMER>
   <CHANNELS />
</SKADI_DATA>
```

## Usage

launch skadi via 

```
java -jar skadi.jar
```
if a double click on the jar does not work.

After skadi launched, you should see the GUI. You can now add an (at this point only twitch.tv is supported) channel to the channel list. If you then select your just added channel you can click on one of the open buttons to lauch the corresponding windows/programs. 
