import java.util.Scanner;
import java.io.*;
import java.net.*;

public class tcpccs {

	public static void main(String[] args) {

		// Check for correct command-line arguments
		if (args.length != 2) {
			System.out.println("Usage: java TCPcssclient <server_hostname> <username>");
			return;
		}

		// Set server and user parameters from command-line arguments
		String serverAddress = args[0];
		String userName = args[1];

		try {
			// Initialize connection to the server
			Socket clientSocket = new Socket(serverAddress, 12345);
			System.out.println("Connected to the server. Start messaging or type '/quit' to exit.");

			// Set up input/output streams
			BufferedReader serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			DataOutputStream serverWriter = new DataOutputStream(clientSocket.getOutputStream());

			// Send username to the server
			serverWriter.writeBytes(userName + "\n");

			// Thread to handle incoming messages from server
			new Thread(new ServerListener(clientSocket, serverReader)).start();

			// Main loop for sending messages from client to server
			try (Scanner userInput = new Scanner(System.in)) {
				while (true) {
					String message = userInput.nextLine();
					if ("/quit".equalsIgnoreCase(message.trim())) { // Exits if user types '/quit'
						break;
					}
					serverWriter.writeBytes(message + "\n"); // Send message to server
				}
			}
			clientSocket.close();
			System.out.println("Disconnected from server.");

		} catch (IOException e) {
			System.out.println("Connection error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Separate class to handle incoming messages from the server
	private static class ServerListener implements Runnable {
		private final Socket clientSocket;
		private final BufferedReader serverReader;

		// Constructor for initializing socket and input stream
		public ServerListener(Socket socket, BufferedReader serverReader) {
			this.clientSocket = socket;
			this.serverReader = serverReader;
		}

		// Continuously read and display server messages
		@Override
		public void run() {
			try {
				String serverMessage;
				while ((serverMessage = serverReader.readLine()) != null) {
					System.out.println(serverMessage); // Print each incoming message
				}
			} catch (SocketException e) {
				System.out.println("Server connection closed.");
			} catch (IOException e) {
				System.out.println("Error receiving message from server.");
				e.printStackTrace();
			} finally {
				// Close socket if disconnected
				try {
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
