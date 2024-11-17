import java.io.*;
import java.net.*;
import java.util.*;

public class tcpcss {

	// Manages client connections
	private static ClientManager clientManager = new ClientManager();

	public static void main(String[] args) {

		System.out.println("Listener on port 12345");
		System.out.println("Waiting for connections ...");

		try (ServerSocket serverSocket = new ServerSocket(12345)) {

			// Continuously accept client connections
			while (true) {
				Socket clientSocket = serverSocket.accept();

				// Start each client on a uniquely named thread
				clientManager.addClient(clientSocket);
			}
		} catch (IOException e) {
			System.out.println("Server error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}

// ClientManager to handle client connections and broadcast messages
class ClientManager {

	private final List<ClientHandler> activeClients = Collections.synchronizedList(new ArrayList<>());
	private int clientCounter = 0;

	// Add a new client and start a handler thread
	public void addClient(Socket clientSocket) {
		ClientHandler clientHandler = new ClientHandler(clientSocket, clientCounter++, this);
		activeClients.add(clientHandler);

		// Create a new thread with a unique name for each client connection
		Thread clientThread = new Thread(clientHandler, "Thread-" + (clientCounter - 1));
		System.out.println("New connection, thread name is " + clientThread.getName() +
				", ip is: " + clientSocket.getInetAddress().getHostAddress() +
				", port: " + clientSocket.getPort());

		System.out.println("Adding to list of sockets as " + (clientCounter - 1));
		clientThread.start(); // Start the thread with the specified name
	}

	// Broadcast message to all clients except the sender
	public void broadcast(String message, ClientHandler sender) {
		synchronized (activeClients) {
			for (ClientHandler client : activeClients) {
				if (client != sender) {
					client.sendMessage(message);
				}
			}
		}
	}

	// Remove a client upon disconnection
	public void removeClient(ClientHandler clientHandler) {
		activeClients.remove(clientHandler);
		System.out.println("Host disconnected, ip " + clientHandler.getClientSocket().getInetAddress().getHostAddress() +
				", port " + clientHandler.getClientSocket().getPort());
	}
}

// ClientHandler for each client connection
class ClientHandler implements Runnable {

	private final Socket clientSocket;
	private String clientName;
	private final DataOutputStream output;
	private final ClientManager clientManager;

	// Constructor to initialize client connection and output stream
	public ClientHandler(Socket socket, int clientId, ClientManager manager) {
		this.clientSocket = socket;
		this.clientManager = manager;
		try {
			this.output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException("Error initializing output stream", e);
		}
	}

	// Main method to handle communication with the client
	@Override
	public void run() {
		try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
			clientName = input.readLine();

			String message;
			// Read messages from the client
			while ((message = input.readLine()) != null) {
				String formattedMessage = "client: " + clientName + ": " + message;
				System.out.println(formattedMessage); // Print message to server console
				clientManager.broadcast(formattedMessage, this); // Broadcast message to other clients
			}
		} catch (SocketException e) {
			System.out.println("Client disconnected.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			clientManager.removeClient(this); // Remove client from manager
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Send message to this client
	public void sendMessage(String message) {
		try {
			output.writeBytes(message + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Get the client socket
	public Socket getClientSocket() {
		return clientSocket;
	}
}
