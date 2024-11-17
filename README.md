# TCP Chat Application with Threading

This project is a Java-based TCP chat application that allows multiple clients to connect to a server and communicate in real time. The server handles client connections, broadcasts messages, and manages join/leave notifications, while the clients enable users to send and receive messages seamlessly.

## Features

- **Multi-Client Support:** Connect multiple clients to the chat server simultaneously.
- **Broadcast Messaging:** Messages from a client are relayed to all connected clients.
- **Join/Leave Notifications:** Notify all clients when a new client joins or leaves the chat.
- **Threading for Concurrency:** Separate threads handle each client connection for efficient communication.
- **Graceful Shutdown:** Server and clients can handle disconnections gracefully.
- **One-to-One Messaging Support:** Allow users to send private messages to specific clients.

## Learning Goals

- Understand TCP sockets and client-server architecture.
- Implement threading for managing multiple client connections.
- Practice string manipulation and message protocol design.

## Setup Instructions

### Prerequisites

- Java Development Kit (JDK) installed.
- Basic knowledge of Java and network programming.
