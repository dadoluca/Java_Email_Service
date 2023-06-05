JAVA EMAIL SERVICE

Sono stati utilizzati in questo progetto:
- JavaFx
- sockets
- Java Reflection
- Meccansimi basati sul pattern Observer Observable
- programmazione multi-thread:
	- meccanismi di sincronizzazione lato client (blocco synchronized(lock){}) e lato server (metodi synchronized)
	- meccanismi di sincronizzazione wait() and signal() per attendere l'esecuzione di operazioni


--------------------------------------------------------------------------------------------------------

TRACCIA PROGETTO:

Si sviluppi un’applicazione Java che implementi un servizio di posta elettronica organizzato con un mail server che gestisce le caselle di posta elettronica degli utenti e i mail client necessari per permettere agli utenti di accedere alle proprie caselle di posta.

Il mail server gestisce una lista di caselle di posta elettronica e ne mantiene la persistenza utilizzando file (txt o binari, a vostra scelta, non si possono usare database) per memorizzare i messaggi in modo permanente.

Il mail server ha un’interfaccia grafica sulla quale viene visualizzato il log delle azioni effettuate dai mail clients e degli eventi che occorrono durante l’interazione tra i client e il server.

Per esempio: apertura/chiusura di una connessione tra mail client e server, invio di messaggi da parte di un client, ricezione di messaggi da parte di un client, errori nella consegna di messaggi.
NB: NON fare log di eventi locali al client come per esempio il fatto che ha schiacciato un bottone, aperto una finestra o simili in quanto non sono di pertinenza del server.
Una casella di posta elettronica contiene:

Nome dell’account di mail associato alla casella postale (es.giorgio@mia.mail.com).
Lista (eventualmente vuota) di messaggi. I messaggi di posta elettronica sono istanze di una classe Email che specifica ID, mittente, destinatario/i, argomento, testo e data di spedizione del messaggio.
Il mail client, associato a un particolare account di posta elettronica, ha un’interfaccia grafica così caratterizzata:

L’interfaccia permette di:
creare e inviare un messaggio a uno o più destinatari (destinatari multipli di un solo messaggio di posta elettronica)
leggere i messaggi della casella di posta
rispondere a un messaggio ricevuto, in Reply (al mittente del messaggio) e/o in Reply-all (al mittente e a tutti i destinatari del messaggio ricevuto)
girare (forward) un messaggio a uno o più account di posta elettronica
rimuovere un messaggio dalla casella di posta.
L’interfaccia mostra sempre la lista aggiornata dei messaggi in casella e, quando arriva un nuovo messaggio, notifica l’utente attraverso una finestra di dialogo.

NB: per semplicità si associno i mail client agli utenti a priori: non si richiede che il mail client offra le funzionalità di registrazione di un account di posta. Inoltre, un mail client è associato a una sola casella di posta elettronica e la sua interfaccia non richiede autenticazione da parte dell’utente.

NB: il mail client non deve andare in crash se il mail server viene spento – gestire i problemi di connessione al mail server inviando opportuni messaggi di errore all’utente e fare in modo che il mail client si riconnetta automaticamente al server quando questo è novamente attivo.

Requisiti tecnici:
L’applicazione deve essere sviluppata in Java (JavaFXML) e basata su architettura MVC, con Controller + viste e Model, seguendo i principi del pattern Observer Observable. Si noti che non deve esserci comunicazione diretta tra viste e model: ogni tipo di comunicazione tra questi due livelli deve essere mediato dal controller o supportata dal pattern Observer Observable. Non si usino le classi deprecate Observer.java e Observable.java. Si usino le classi di JavaFX che supportano il pattern Observer Observable.
L’applicazione deve permettere all’utente di correggere eventuali input errati (per es., in caso di inserimento di indirizzi di posta elettronica non esistenti, il server deve inviare messaggio di errore al client che ha inviato il messaggio; inoltre, in caso di inserimento di indirizzi sintatticamente errati il client stesso deve segnalare il problema senza tentare di inviare i messaggi al server).
I client e il server dell’applicazione devono parallelizzare le attività che non necessitano di esecuzione sequenziale e gestire gli eventuali problemi di accesso a risorse in mutua esclusione. NB: i client e il server di mail devono essere applicazioni java distinte; la creazione/gestione dei messaggi deve avvenire in parallelo alla ricezione di altri messaggi.
L’applicazione deve essere distribuita (i mail client e il server devono stare tutti su JVM distinte) attraverso l’uso di Socket Java.

--------------------------------------------------------------------------------------------------------

FUNZIONAMENTO generala della comunicazione client-server: 

Il server genera un thread requestsListener che rimane in ascolto di connsessioni, ogni volta che un client apre una connessione attraverso un socket, il server esegue la socket.accept(), e genera un thread clientRequestHandler che si occuperà d'ora in poi di servire quel client e a cui viene passato quel determinato socket e il model (comune a tutti i clientRequestHandler).
Essendo che i threads operano sullo stesso model i metodi del model che accedono a variabili condivise sono synchronized(in modo da preservare l'integrità dei dati e garantirne l'accesso in mutua esclusione) (sincronizzazione lato server)
Ora questo clientRequestHandler si metterà in ascolto di messaggi da parte di questo specifico utente fino a quando esso non manderà un messaggio di logout.
Come ragionevole che sia, il primo messaggio che riceverà da un client sarà il comando di login, esso potrà quindi verificare nel model che l' email address esista e in caso affermativo restituirgli la propria lista di email ricevute in modo che egli possa visualizzarle nella propria mailbox. Inoltre salva anche il socket e gli stream in una struttura HashMap clients_sockets nel model condivisa tra tutti i clientRequestHandler (Map<String, SocketInfo> clients_sockets). 
Quando un client invia una mail, il suo clientRequestHandler si occuperà di controllare se i recipients esistono, se sì salvarla in un csv di emails, stamparla nel log server e poi controllare attraverso la struttura clients_sockets se ci sono le connessioni aperte con i client riceventi (ovvero se i riceventi sono online) e in tal caso invia loro la email in modo che possano visualizzarla in realtime.
Il client, una volta che il login ha successo, crea un thread che rimane in attesa di messaggi dal server e che quindi riceverà le nuoeve emails. Se il client rileva che il socket è stato chiuso perché il server è andato offline allora entra in un while in cui ogni tot secondi riproverà a riconnettersi. 

Mailbox ha una lista osservabile di mail, in modo che gli observer (ListView della View) possano mutare quando aggiungo elementi alla lista e un metodo che ritorna un cast della lista in ArrayList in modo che tale lista sia serializzabile e mandabile via socket al client, una volta loggato.


--------------------------------------------------------------------------------------------------------

TESTING
Per testare l'applicativo si possono usare come indirizzi email quelli scritti nel file user.txt (luca.dadone@gmail.com ..)