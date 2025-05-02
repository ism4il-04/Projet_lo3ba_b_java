package io.github.lo3ba;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.lo3ba.multiplayer.Client;
import io.github.lo3ba.multiplayer.Server;
import io.github.lo3ba.network.ChatClient;
import io.github.lo3ba.network.ChatServer;
import io.github.lo3ba.screens.MainMenuScreen;
import io.github.lo3ba.screens.MenuScreen;
import io.github.lo3ba.screens.MultiplayerScreen;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main_Game extends Game {
    private SpriteBatch batch;
    private ChatClient chatClient;
    private ChatServer chatServer;
    private Client client;
    private Server server;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));
        //initializeNetwork();
        /*try {
            Socket socket = new Socket("localhost",1234);
            setScreen(new MultiplayerScreen(this,new Client(socket,"Player_" + (int)(Math.random()*1000))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    private void initializeNetwork() {
        new Thread(() -> {
            try {
                chatServer = new ChatServer();
                chatServer.start(12345, this::onServerReady);
            } catch (IOException e) {
                Gdx.app.error("NETWORK", "Server failed to start", e);
                startClientWithRetry();
            }
        }).start();
    }

    private void onServerReady() {
        Gdx.app.log("NETWORK", "Server ready - starting client");
        startClientWithRetry();
    }

    private void startClientWithRetry() {
        new Thread(() -> {
            int maxAttempts = 3;
            int attempt = 0;
            boolean connected = false;

            while (!connected && attempt < maxAttempts) {
                attempt++;
                chatClient = new ChatClient("Player_" + (int)(Math.random()*1000));
                connected = chatClient.connect("localhost", 12345);

                if (!connected && attempt < maxAttempts) {
                    try {
                        Thread.sleep(1000 * attempt);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (connected) {
                Gdx.app.log("NETWORK", "Client connected successfully");
            } else {
                Gdx.app.error("NETWORK", "Failed to connect after " + maxAttempts + " attempts");
            }
        }).start();
    }

    public SpriteBatch getBatch() { return batch; }
    public ChatClient getChatClient() { return chatClient; }

    @Override
    public void dispose() {
        if (chatClient != null) chatClient.disconnect();
        if (chatServer != null) chatServer.stop();
        if (batch != null) batch.dispose();
    }
}
