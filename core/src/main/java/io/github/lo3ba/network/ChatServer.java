package io.github.lo3ba.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private ServerSocket serverSocket;
    private Set<PrintWriter> clientWriters = new HashSet<>();
    private boolean isRunning = false;

    public void start(int port, Runnable onReady) throws IOException {
        serverSocket = new ServerSocket(port);
        isRunning = true;
        System.out.println("Server started on port: " + port);

        if (onReady != null) {
            onReady.run();
        }

        while (isRunning) {
            try {
                new ClientHandler(serverSocket.accept(), clientWriters).start();
            } catch (SocketException e) {
                if (isRunning) {
                    System.err.println("Server socket error: " + e.getMessage());
                }
            }
        }
    }

    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }

    public boolean isRunning() {
        return isRunning && serverSocket != null && !serverSocket.isClosed();
    }
}
