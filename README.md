# WiFiDirect PingPong

## Informations
WiFiDirect PingPong (aka PingPong) is a demostrative Android's application that try to overcome some WiFi-Direct limitations.
I driver degli smarphone commerciali disponibili, al momento, non permettono ad un device di partecipare contemporaneamente a due gruppi WiFi Direct.
Questa app cerca di superare questo limite facendo si che un dispositivo "salti" come una pallina da ping pong da un gruppo ad un altro tramite continue connessione e riconnessioni.

PingPong è stata creata per Android 4.4 (KitKat) o superiore. Questa scelta è legata al fatto che WiFiDirect nelle versioni precedenti si è dimostrato troppo instabile ed inaffidabile.

E' importante ricordare che si tratta di una applicazione dimostrativa, quindi funzionalità come la gestione della rotazione dello schermo, standby del dispositivo, wifi disattivato and so on, non sono gestite come in un prodotto commerciale.

## Results
VIDEO YOUTUBE 

Come si nota dal video, l'app funziona perfettamente, ma con performance inaccettabili.<br/>
Il problema è la fase di Discovery del protocollo / l'implementazione di WiFi-Direct in Android, infatti:<br/>
1. Il tempo di ricerca dei dispositivi è troppo elevato<br/>
2. Dopo un certo tempo, il dispositivo non risulta più rilevabile dagli altri, quindi bisogna riavviare la Discovery su tutti i device<br/>
3. A volte la parte wifi di Android crasha e l'unico modo per risolvere il problema è un completo reboot del dispositivo (questa sitauzione è riconoscibile dalle impostazioni Wifi di android, dove improvvisamente non risulta più un grado di scansionare le rete wifi)

Questo mostra come la fase di Discovery del protocollo WiFi Direct sia il tallone di Achille di ogni progetto mirato ad estendere le funzione e superarne i limiti.
Per ovviare al problema, è necessario attendere il rilascio della nuova versione del protocollo con l'utilizzo di (NAN) [https://www.wi-fi.org/wi-fi-nan-technical-specification-draft-v0024], ancora in versione draft e non implementata in Android.


## News
- *02/27/2015* - **PingPong** public release


## Features
1. **Cambiare il nome del dispositivo** tramite Java Reflection
2. Visualizzazione dei dispositivi nelle vicinanze
3. Connessione / disconnessione tra dispositivi
4. Connessione di più client allo stesso gruppo owner
5. I client possono visualizzare i dati del proprio Group Owner
6. I Group Owner posso visualizzare la lista e i dati dei propri peers
7. I client possono **Inviare file video dalla galleria** al proprio Group Owner
8. **"Eternal Discovery"** che permette il riavvio della fase di Discovery ad ogni evento di disconnessione o errore.
9. **I client possono diventare "PingPong Device"** scegliendo il GO di partenza (Ping) e quello di arrivo (Pong) e iniziando a connettersi e disconnettersi in modo alternato dai 2 group owner, senza interferire col funzionamento degli altri peer

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
