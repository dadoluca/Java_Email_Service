# Java Email Service
This project is an implementation of a Java application that provides an email service organized with a mail server responsible for managing users' mailboxes and the necessary mail clients for users to access their mailboxes.
### Technologies Used:

- MVC pattern
- JavaFx
- Sockets
- Java Reflection
- Observer Observable pattern-based mechanisms
- Multi-threaded programming:
  - Client-side synchronization mechanisms (synchronized(lock) {})
  - Server-side synchronization methods (synchronized)
  - Wait() and signal() synchronization mechanisms for operation execution waiting

    
## Features

### Mail Server
- Manages a list of email mailboxes and maintains their persistence using files (text or binary).
- Provides a graphical interface displaying the log of actions performed by mail clients and events occurring during client-server interaction.
- Logs actions such as opening/closing connections between mail clients and the server, sending and receiving messages, and errors in message delivery.

### Email Mailbox
- Contains the email account name associated with the mailbox.
- Stores a list of messages. Each email message is an instance of the Email class, specifying ID, sender, recipient(s), subject, text, and sending date.

### Mail Client
- Associated with a specific email account.
- Provides a graphical interface allowing users to:
  - Create and send messages to one or more recipients.
  - Read mailbox messages.
  - Reply to received messages (Reply or Reply-all).
  - Forward messages to one or more email accounts.
  - Remove messages from the mailbox.
- Displays an updated list of messages in the mailbox and notifies the user through a dialog window when a new message arrives.

## General Operation of Client-Server Communication:

The server generates a requestsListener thread that listens for connections. Whenever a client opens a connection through a socket, the server executes socket.accept(), and generates a clientRequestHandler thread that serves that client and is passed that particular socket and the model (common to all clientRequestHandlers). Since the threads operate on the same model, methods accessing shared variables in the model are synchronized (to preserve data integrity and ensure access in mutual exclusion) (server-side synchronization). Each clientRequestHandler listens for messages from that specific user until it receives a logout message. The first message it receives from a client is the login command. The clientRequestHandler then verifies in the model that the email address exists and, if so, returns its list of received emails so that the user can view them in their mailbox. It also saves the socket and streams in a HashMap structure clients_sockets in the model shared among all clientRequestHandlers (Map<String, SocketInfo> clients_sockets). When a client sends an email, its clientRequestHandler checks if the recipients exist, saves the email in an email csv, prints it in the server log, and then checks through the clients_sockets structure if there are open connections with the receiving clients (i.e., if the recipients are online), and if so, sends them the email so they can view it in real-time. Once login is successful, the client creates a thread that waits for messages from the server and thus receives the new emails. If the client detects that the socket has been closed because the server has gone offline, it enters a while loop where every few seconds it tries to reconnect.

The Mailbox has an observable list of emails, so that the observers (ListView of the View) can change when I add elements to the list, and a method that returns a cast of the list to ArrayList so that such list is serializable and can be sent via socket to the client, once logged in.

**TESTING:** To test the application, you can use the email addresses listed in the user.txt file (luca.dadone@gmail.com, riad.muska@gmail.com, davide.benotto@gmail.com).
