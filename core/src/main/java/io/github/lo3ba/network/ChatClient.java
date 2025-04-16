package io.github.lo3ba.network;

import com.badlogic.gdx.Gdx;
import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class ChatClient {
    private final String username;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Consumer<String> messageHandler;

    public ChatClient(String username) {
        this.username = username;
    }

    public boolean connect(String host, int port) {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 3000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(this::listenForMessages).start();
            return true;
        } catch (IOException e) {
            Gdx.app.error("NETWORK", "Connection failed: " + e.getMessage());
            return false;
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (messageHandler != null) {
                    String finalMessage = message;
                    Gdx.app.postRunnable(() -> messageHandler.accept(finalMessage));
                }
            }
        } catch (IOException e) {
            Gdx.app.error("NETWORK", "Connection lost: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        if (isConnected()) {
            out.println(username + ": " + message);
        }
    }

    public void setOnMessageReceivedListener(Consumer<String> handler) {
        this.messageHandler = handler;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            Gdx.app.error("NETWORK", "Error disconnecting: " + e.getMessage());
        }
    }
}
