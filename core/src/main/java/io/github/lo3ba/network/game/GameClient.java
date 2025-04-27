package io.github.lo3ba.network.game;

import java.io.*;
import java.net.Socket;
import com.badlogic.gdx.utils.Json;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int playerId;

    public void connect(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Start a thread to listen for server messages
        new Thread(this::listenForMessages).start();
    }

    public void send(Object message) {
        String json = new Json().toJson(message);
        out.println(json);
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                handleMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Connection lost: " + e.getMessage());
        }
    }

    private void handleMessage(String json) {
        // TODO: Parse JSON and update game state
        System.out.println("Received: " + json);
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
}
