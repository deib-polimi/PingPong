# WiFiDirect PingPong

## Informations
WiFiDirect PingPong (aka PingPong) is a demo Android's application that try to overcome some WiFi-Direct limitations.
At the moment, the wifi diver of commercial devices don't allow a device to partecipate simultaneosly in two WiFi-Direct's groups. This app tries to overcome this limitations. The main goal is the possibility for a client to "jump" continously between two different groups, like a ball in the game called ping pong.
It's possibile to achieve this with synchronized connection and disconnection between GO's and the "PingPong client".

PingPong requires Android 4.4 KitKat (API 19) or higher. This choice is related to to the fact that in previous versions, this protocol was unstable and unreliable.

It's important to remember that this is a demo application, so features like the management of screen's rotation, standby device, wifi not available and so on, are not managed as a commercial product.

## Results
VIDEO YOUTUBE 

As you can see, PingPong works with poor performances.<br/>
The main problems are the "Discovery Phase" of this protocol and the WiFi-Direct implementation in Android, in fact:<br/>
1. The discovery time is too high when the number of devices increases <br/>
2. After a certain time, a device is no longer discoverable from others, so you need to restart the Discovery Phase on all devices <br/>
3. Sometimes, the WiFi part of Android crashes and the only way to solve this annoying problem is a complete reboot of the device (this situation is recognizable when Android can't find other network in WiFi Setting's app).

This shows that in some applications, in particular when you need to transfer files or real time applications, the Discovery Phase in Wi-Fi Direct is a very big problem.

## News
- *03/02/2015* - **PingPong alpha** public release


## Features
You can:
1. **change the device name** with Java Reflection
2. show a list of nearby devices
3. manage connection and disconnection between devices
4. connect more than one client to the same GO
5. show GO's information on all clients connected to him
6. show the list of Clients and some other data on every GO
7. **send video files in the gallery** from clients to their GO's
8. **transorm a client into a "PingPong Device"** choosing the "starting GO" called Ping Device and the "destination GO" called Pong Device.  e iniziando a connettersi e disconnettersi in modo alternato dai 2 group owner, senza interferire col funzionamento degli altri peer

## Possibile future extensions

## Usage

### General usage
1. Attivare il WiFi su tutti i dispositivi
2. Aprire l'app su tutti i dispositivi scelti
3. Scegliere i nomi dei dispositivi toccando la "cardview" sotto la scritta "This device"
2. Attendere la ricerca dei dispositivi nelle vicinanze
3. Collegarsi ad un dispositivo toccando l'elemento della lista
4. Sul peer toccare il primo pulsante (gallery button) per scegliere un video dalla galleria ed inviarlo (attendere che la ricezione sia completata)
5. Disconnettere dal GO (tutti i client saranno disconnessi) o dal client (solo quesl client sarà disconnesso) toccando il secondo pulsante (disconnect button)

### Group with multiple peers usage
1. Attivare il WiFi su tutti i dispositivi
2. Aprire l'app su tutti i dispositivi scelti
3. Scegliere i nomi dei dispositivi toccando la "cardview" sotto la scritta "This device"
2. Attendere la ricerca dei dispositivi nelle vicinanze
3. Collegarsi ad un dispositivo toccando l'elemento della lista
4. Toccare sulla seconda icona della toolbar per riavviare la discovery su tutti i dispositivi
5. Collegare altri client, scegliendo nella propria lista "Other Devices" il GO già scelto in precendeza. In questo modo sul GO la lista di peer verrà aggiornata e il nuovo dispositivo aggiunto.

### PingPong usage
1. Attivare il WiFi su tutti i dispositivi
2. Aprire l'app su tutti i dispositivi scelti
3. Scegliere i nomi dei dispositivi toccando la "cardview" sotto la scritta "This device"
2. Attendere la ricerca dei dispositivi nelle vicinanze
3. Collegarsi ai un dispositivo toccando l'elemento della lista
4. Dopo aver creato i 2 gruppi di dispositivi, sceglierne uno come dispositivo che farà pingpong, quindi su tutti gli altri device (eccetto quello scelto per ping pong) 
toccare la prima icona della toolbar, in modo da attivare la pingpong mode ed automatizzare le fasi di discovery
5. Sul peer toccare il terzo pulsante (ping pong button) per aprire il Dialog e scegliere il go di destinazione (il GO del gruppo a cui il dispositivo che vuole fare pingpong non appartiene)
6. Confermare per avviare pingponging


## Interesting things
Questa app cotiene una astrazione degli oggetti P2PDevice, P2PGroup and P2PGroups per renderli più semplici da adattare e utilizzare per implementare funzionalità più complesse di quelle fornite da Android.
Per esempio si può ottenere facilemente l' IP Address associato al GO di un certo P2PGroup.
L'estensione che permette di gestire gli indirizzi ip dei client in WiFi MultiChat si basa su una di queste classi.

Inoltre vi sono altre list che utilizzano Singleton per memorizzare in modo facilmente accessibile da tutto il programma 
i client di un GO, i dispositivi rilevati, la lista di dispositivi con cui poter fare pingpong and so on.

## License

*Stefano*
