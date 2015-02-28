# WiFiDirect PingPong

## Informations
WiFiDirect PingPong (aka PingPong) is a demostrative Android's application that try to overcome some WiFi-Direct limitations.
I driver degli smarphone commerciali disponibili, al momento, non permettono ad un device di partecipare contemporaneamente a due gruppi WiFi Direct.
Questa app cerca di superare questo limite facendo si che un dispositivo "salti" come una pallina da ping pong da un gruppo ad un altro tramite continue connessione e riconnessioni.

PingPong è stata creata per Android 4.4 (KitKat) o superiore. Questa scelta è legata al fatto che WiFiDirect nelle versioni precedenti si è dimostrato troppo instabile ed inaffidabile.

E' importante ricordare che si tratta di una applicazione dimostrativa, quindi funzionalità come la gestione della rotazione dello schermo, standby del dispositivo, wifi disattivato and so on, non sono gestite come in un prodotto commerciale.

## Results
VIDEO YOUTUBE 

Come si nota dal video, l'app funziona perfettamente, ma con performance davvero inaccettabili. 
Il problema è la fase di Discovery del protocollo / l'implementazione di WiFi-Direct in Android, infatti:
1. Il tempo di ricerca dei dispisitivi è troppo elevato
2. Dopo un certo tempo, il dispositivo non risulta più rilevabile dagli altri, quindi bisogna riavviare la Discovery su tutti i device
3. A volte la parte wifi di Android crasha e l'unico modo per risolvere il problema è un completo reboot del dispositivo (questa sitauzione è riconoscibile dalle impostazioni Wifi di android, dove improvvisamente non risulta più un grado di scansionare le rete wifi)

Questo mostra come la fase di Discovery del protocollo WiFi Direct sia il tallone di Achille di ogni progetto mirato ad esterne le funzione e superarne i limiti.
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

## License







``` bash
    $ cd learnDCL
    $ git branch dcl
    $ git checkout dcl
```
* Open the project contained in the root folder with *Android Studio*.
* Read **carefully** the class `MainActivity.java` in the `app` module. 

```
    https://dl.dropboxusercontent.com/blablabla
```


*Stefano*
