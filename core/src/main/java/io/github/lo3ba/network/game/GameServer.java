package io.github.lo3ba.network.game;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import com.badlogic.gdx.utils.Json;

public class GameServer {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private int nextPlayerId = 1;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler client = new ClientHandler(clientSocket, nextPlayerId++);
            clients.add(client);
            new Thread(client).start();
        }
    }

    public void broadcast(Object message) {
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private int playerId;

        public ClientHandler(Socket socket, int playerId) {
            this.socket = socket;
            this.playerId = playerId;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Notify the client of their ID
                send(new GameMessage.Connect() {{
                    playerId = this.playerId;
                }});

                // Broadcast new player to others
                broadcast(new GameMessage.Connect() {{
                    playerId = this.playerId;
                }});

                // Listen for messages
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received from " + playerId + ": " + message);
                    broadcast(message); // Echo back for testing
                }
            } catch (IOException e) {
                System.err.println("Client " + playerId + " disconnected: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clients.remove(this);
                broadcast(new GameMessage.Disconnect() {{
                    playerId = this.playerId;
                }});
            }
        }

        public void send(Object message) {
            String json = new Json().toJson(message);
            out.println(json);
        }
    }
}
