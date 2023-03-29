Progetto


Uso di java beans e properties per caratterizzare i
dati del model in modo standard.
L’obiettivo è evitare di scrivere codice dettagliato per
visualizzare nella GUI i valori delle property mentre
cambiano, e/o per inizializzare i valori delle property a
partire dai dati acquisiti in input (form) nella GUI.

I Java Beans sono classi java che rispettano uno
standard di definizione dei metodi e delle loro variabili di
istanza (o di stato)
– Per ogni variabile xxx di istanza che vogliamo
esporre come property del javaBean, noi
dobbiamo definire il metodo public Type
getXxx(), per leggere valore della property.
– Se vogliamo permettere di modificare il valore
della property, dobbiamo definire anche il metodo
public void setXxx(), per assegnare un valore
alla property





Mail client e Mail server, che sono 2 applicazioni separate che comunicheranno attraverso il socket.
Il Mail Server, che è quello da cui partire, è un oggetto che gestisce una lista di caselle di posta elettronica.
Si userà un file per salvare le mail, un file strutturato es XML.
Il server avrà un'interfaccia grafica sempliciotta dove si può accendere e spegnere il serevr e
vedere il log ovvero il tracciamento delle mail scambiate.
Architettura a stella, non è peer to peer non è una chat.
Viene chiesta la in box non messaggi cancellati ecc.

Client, uno per utente, interfaccia fatta con java fx
Deve inviare un messaggio collegandosi al server, cancellare messaggi ecc
Dopo ogni operazione aggiorno lo stato della mail box su un server.
Meglio se non è un server POP ovvero un server che non mantiene la inbox sul server ma mantiene solo in locale sul client

Server che può leggere direttamente da file, posso già pensare alla struttura, un file per utente(????)
Client deve essere in grade di connettersi al server se questo va giù e poi risale.

Specialmente il client deve essere fatto con MVC ma non Observable ecc



Sockets fanno comunicare terminale client e terminale server attraverso IP e Porta.

un server deve crare un un pool di thrad, ciascun thread serve un client
per trasmettere un oggetto bisogna usare ObjectInputStream e ObjectOutputStream
quando si trasmettono gli oggetti sul socket, questi devono essere serializable quindi le 
proprety non possono essere trasferite.

Le librerie devono essere contenute sia nel client che nel server.
Es classe Email dobbiamo metterla in entrambi i progetti.
I 2 progetti non possono leggere dagli stessi file!! Devono essere cartelle diverse!


