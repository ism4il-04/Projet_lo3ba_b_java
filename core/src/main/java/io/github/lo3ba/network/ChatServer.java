package io.github.lo3ba.network;

import io.github.lo3ba.network.ClientHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private ServerSocket serverSocket;
    private Set<PrintWriter> clientWriters = new HashSet<>();

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port: " + port);

        while (!serverSocket.isClosed()) {
            try {
                new ClientHandler(serverSocket.accept(), clientWriters).start();
            } catch (SocketException e) {
                if (!serverSocket.isClosed()) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
