skadi
=====
Skadi is a lightweight toolkit that allows you to comfortably watch twitch.tv channels via livestreamer/vlc. It also allows you to directly open the popup-chat of the channels you are watching without having to first open the twitch url and open it up manually. Skadi can import the channels you follow from twitch.tv so you don't have to add them all by hand.

![skadi screenshot](https://i.imgur.com/ZFhXWOQ.png "Skadi screenshot")

## Download

see https://github.com/s1mpl3x/skadi/releases

## Planned features

* minimize to tray
* show message if saved channel goes live
* settings dialog

## Required software
You need to have [Java 1.7+](https://www.java.com/de/download/) installed, as well as [Chrome](https://www.google.com/chrome/) and [livestreamer](https://github.com/chrippa/livestreamer/releases)  set up.

## Setup
If skadi does not launch with the default values, you might need to change them in the settings file which you can find at
```
{userhome}/.skadi/skadi_data.xml 
```
You have to provide the paths to the Chrome and livestreamer executables. If you have livestreamer in you system path, you might also use 'livestreamer' as path

### Example skadi_data.xml for windows
```
<?xml version="1.0" encoding="UTF-8"?>
<SKADI_DATA VERSION="1.0">
   <EXECUTABLES>
      <CHROME>C:\Program Files (x86)\Google\Chrome\Application\chrome.exe</CHROME>
      <LIVESTREAMER>C:\Program Files (x86)\Livestreamer\livestreamer.exe</LIVESTREAMER>
   </EXECUTABLES>
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
   </EXECUTABLES>
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
