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
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(this::listenForMessages).start();
            return true;
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return false;
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null && messageHandler != null) {
                messageHandler.accept(message);
            }
        } catch (IOException e) {
            System.err.println("Connection lost: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            Gdx.app.log("NETWORK", "Sending: " + message);
            out.println(username + ": " + message);
            out.flush();
        } else {
            Gdx.app.error("NETWORK", "Failed to send message");
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
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
}
