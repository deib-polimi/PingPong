# PingPong

![alt tag](http://www.stefanocappa.it/publicfiles/Github_repositories_images/PingPong/pingpong_header_github.png)

<br>

## Informations
Wi-Fi Direct PingPong (aka PingPong) is a demo Android's application that try to overcome some Wi-Fi Direct's limitations.
At the moment, the wifi diver of commercial devices doesn't allow a device to partecipate concurrently in two Wi-Fi Direct's groups. This app tries to overcome these limitations. The main goal is the possibility for a client to "jump" continuously between two different groups, like a ball in the game called ping pong.
It's possible to achieve this with synchronized connections and disconnections between GOs and the "PingPong client".

PingPong requires Android 4.4 KitKat (API 19) or higher. This choice is related to to the fact that in previous versions, this protocol was unstable and unreliable.

It's important to remember that this is a demo application, so features like the management of screen's rotation, standby device, wifi not available and so on, are not managed as a commercial product.


## Requirements
- AndroidStudio
- **Lombok (automatically downloaded as gradle dependency) + Lombok plugin for IntelliJ / AndroidStudio [Available here](https://plugins.jetbrains.com/plugin/6317)**


## Results

[![ScreenShot](http://www.stefanocappa.it/publicfiles/Github_repositories_images/PingPong/youtube-video-piggpong.png)](https://www.youtube.com/watch?v=qsFmHGitniw)

As you can see, PingPong works with poor performances.<br/>
The main problems are the "Discovery Phase" of this protocol and the Wi-Fi Direct's implementation in Android, in fact:<br/>
1. The discovery time is too high when the number of devices increases <br/>
2. After a certain time, a device is no longer discoverable from others, so you need to restart the Discovery Phase on all devices <br/>
3. Sometimes, especially in KitKat, the WiFi part of Android crashes and the only way to solve this annoying problem is a complete reboot of the device (this situation is recognizable when Android can't find other network in Wi-Fi Setting's app).

This shows that in some applications, in particular when you need to transfer files or real time applications, the Discovery Phase in Wi-Fi Direct is a very big problem.

## News
- *03/13/2015* - **PingPong alpha 3** with better log with timestamps
- *03/04/2015* - **PingPong alpha 2** with autonomous group owner, released
- *03/02/2015* - **PingPong alpha** public release


## Features
You can:<br/>
1. **change the device name** with Java Reflection <br/>
2. show a list of nearby devices <br/>
3. manage connection and disconnection between devices <br/>
4. connect more than one client to the same GO <br/>
5. show GO's information on all clients connected to him <br/>
6. show the list of Clients and some other data on every GO <br/>
7. **send video files in the gallery** from clients to their GO's <br/>
8. **transform a client into a "PingPong Device"** choosing the "starting GO" called Ping Device and the "destination GO" called Pong Device. The PingpPong Device connects and disconnects to the Ping Device and Pong Device alternately, i.e. "jumps" ;) <br/>
9. use the **Ping Pong mode** a resized version of the "Eternal Discovery" in "Pigeon Messenger" that tries to synchronize the devices <br/>

## Images
<br/>
![alt tag](http://www.stefanocappa.it/publicfiles/Github_repositories_images/PingPong/discovered-connecting-connected.png)
<br/>
![alt tag](http://www.stefanocappa.it/publicfiles/Github_repositories_images/PingPong/round_button_completo.png)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
![alt tag](http://www.stefanocappa.it/publicfiles/Github_repositories_images/PingPong/toolbar_icon.png)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
![alt tag](http://www.stefanocappa.it/publicfiles/Github_repositories_images/PingPong/change_device_name.png)
<br/>

## Usage

### General usage
1. Activate Wi-Fi on all devices
2. Open this app on all devices
3. Touch the "cardview" under the words "This Device" to choose the device name
4. Wait until devices are discovered
5. Connect your device to another one touching an element in the list under the words "Other Devices"
6. On the Client touch the first button (gallery button) to send a video from your gallery (please wait some seconds, i didn't implement a progress bar :))
7. Disconnect from GO (obviously all clients will be disconnected) or from Client (only this client will be disconnected) touching the second button (disconnect button)

### Group with multiple peers usage
1. Activate Wi-Fi on all devices
2. Open this app on all devices
3. Touch the "cardview" under the words "This Device" to choose the device name
4. Wait until devices are discovered
5. Connect your device to another one touching an element in the list under the words "Other Devices"
4. On every device, touch the second toolbar's icon to restart the Discovery Phase
5. Connect other Clients choosing always the same GO. In this way on the GO you will see his Client's list.

### PingPong usage
1. Activate Wi-Fi on all devices
2. Open this app on all devices
3. Touch the "cardview" under the words "This Device" to choose the device name
4. Wait until devices are discovered
5. Connect your device to another one touching an element in the list under the words "Other Devices"
4. After the group's creation, choose one of them as "PingPong Device" and in every other devices touch the first toolbar's icon to activate the pingpong mode. This in extremely similar to the "Eternal Discovery" in Pigeon Messenger, but here the performances are lower due to synchronization's problems and the only way to achieve this is with this trick that i called "PingPong mode"
5. On the Client touch the third button (ping pong button) to open a dialog and choose the destination GO (the GO of the groups where the device want to be a Client during "pingponging")
6. Confirm to start "PingPong"


## Interesting things
This app contains an abstraction of Android's p2p object, that i called: P2PDevice, P2PGroup and P2PGroups to make it easily maintainable and easy to extend. For example you can obtain easily the IP Address associated to the GO in a P2PGroup.
I created the logic to obtain IP Addresses in Pigeon Messenger extending these classes.

Moreover, there are some lists that use Singleton's pattern:
- the Client list of a GO
- the discovered devices
- the device list used by Ping Pong functionality

## License

Copyright 2015 Stefano Cappa, Politecnico di Milano

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

<br/>
**Created by Stefano Cappa**
