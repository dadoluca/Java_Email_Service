
# Java Email Service
Questo progetto è un'implementazione di un'applicazione Java che fornisce un servizio di posta elettronica organizzato con un server di posta responsabile della gestione delle caselle di posta degli utenti e dei client di posta necessari per consentire agli utenti di accedere alle proprie caselle di posta.

### Tecnologie Utilizzate:
- `JavaFx`
- `Sockets`
- Pattern `MVC`
- `Java Reflection`
- Meccanismi basati sul pattern `Observer Observable`
- Programmazione `multi-threaded`:
  - Meccanismi di sincronizzazione lato client (synchronized(lock) {})
  - Metodi di sincronizzazione lato server (synchronized)
  - Meccanismi di sincronizzazione wait() e signal() per l'attesa dell'esecuzione di operazioni

## Caratteristiche

### Server di Posta
- Gestisce una lista di caselle di posta elettronica e ne mantiene la persistenza utilizzando file (testo o binari).
- Fornisce un'interfaccia grafica che visualizza il log delle azioni eseguite dai client di posta e degli eventi che si verificano durante l'interazione client-server.
- Registra azioni come l'apertura/chiusura delle connessioni tra i client di posta e il server, l'invio e la ricezione di messaggi e gli errori nella consegna dei messaggi.

### Casella di Posta Elettronica
- Contiene il nome dell'account di posta elettronica associato alla casella di posta.
- Memorizza una lista di messaggi. Ogni messaggio di posta elettronica è un'istanza della classe Email, specificando ID, mittente, destinatario/i, oggetto, testo e data di invio del messaggio.

### Client di Posta
- Associato a un account di posta elettronica specifico.
- Fornisce un'interfaccia grafica che consente agli utenti di:
  - Creare e inviare messaggi a uno o più destinatari.
  - Leggere i messaggi nella casella di posta.
  - Rispondere ai messaggi ricevuti (Reply o Reply-all).
  - Inoltrare messaggi a uno o più account di posta elettronica.
  - Rimuovere messaggi dalla casella di posta.
- Mostra un elenco aggiornato dei messaggi nella casella di posta elettronica e notifica all'utente attraverso una finestra di dialogo quando arriva un nuovo messaggio.

## Funzionamento Generale della Comunicazione Client-Server:

Il server genera un thread requestsListener che rimane in ascolto di connessioni. Ogni volta che un client apre una connessione attraverso un socket, il server esegue socket.accept(), e genera un thread clientRequestHandler che serve quel client e gli viene passato quel socket particolare e il modello (comune a tutti i clientRequestHandler). Poiché i thread operano sullo stesso modello, i metodi che accedono a variabili condivise nel modello sono sincronizzati (per preservare l'integrità dei dati e garantire l'accesso in mutua esclusione) (sincronizzazione lato server). Ogni clientRequestHandler ascolta i messaggi da quel determinato utente fino a quando non riceve un messaggio di logout. Il primo messaggio che riceve da un client è il comando di login. Il clientRequestHandler verifica quindi nel modello che l'indirizzo email esista e, in caso affermativo, restituisce la lista dei messaggi ricevuti in modo che l'utente possa visualizzarli nella propria casella di posta. Salva anche il socket e i flussi in una struttura HashMap clients_sockets nel modello condivisa tra tutti i clientRequestHandler (Map<String, SocketInfo> clients_sockets). Quando un client invia una email, il suo clientRequestHandler controlla se i destinatari esistono, salva l'email in un csv di email, la stampa nel log del server e quindi controlla attraverso la struttura clients_sockets se ci sono connessioni aperte con i client riceventi (cioè se i destinatari sono online), e in tal caso invia loro l'email in modo che possano visualizzarla in tempo reale. Una volta effettuato il login con successo, il client crea un thread che attende i messaggi dal server e quindi riceve le nuove email. Se il client rileva che il socket è stato chiuso perché il server è andato offline, entra in un ciclo while dove ogni pochi secondi cerca di riconnettersi.

La casella di posta ha un elenco osservabile di email, in modo che gli osservatori (ListView della View) possano cambiare quando vengono aggiunti elementi all'elenco, e un metodo che restituisce un cast dell'elenco in ArrayList in modo che tale elenco sia serializzabile e possa essere inviato tramite socket al client, una volta effettuato il login.

**TESTING:** Per testare l'applicazione, è possibile utilizzare gli indirizzi email elencati nel file user.txt (luca.dadone@gmail.com, riad.muska@gmail.com, davide.benotto@gmail.com).
