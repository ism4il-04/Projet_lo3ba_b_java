package io.github.lo3ba;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.lo3ba.network.ChatClient;
import io.github.lo3ba.network.ChatServer;
import io.github.lo3ba.screens.GameScreen;
import io.github.lo3ba.screens.MainMenuScreen;

import java.io.IOException;

public class Main_Game extends Game {
    private SpriteBatch batch;
    private ChatClient chatClient;
    private ChatServer chatServer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));

        chatClient = new ChatClient("Player_" + (int)(Math.random() * 1000));


        new Thread(() -> {
            try {
                Thread.sleep(10000); // delay bach itloada server
                boolean connected = chatClient.connect("localhost", 12345);
                Gdx.app.postRunnable(() -> {
                    if (connected) {
                        setScreen(new GameScreen(this));
                    } else {
                        Gdx.app.error("Main", "Failed to connect chat");
                        setScreen(new GameScreen(this)); // Charge quand même le jeu
                    }
                });
            } catch (InterruptedException e) {
                Gdx.app.error("Main", "Connection thread interrupted", e);
            }
        }).start();
    }

    @Override
    public void render() {
        super.render();
    }

    private void startNetwork() {
        new Thread(() -> {
            try {
                chatServer = new ChatServer();
                chatServer.start(12345);
                Gdx.app.log("Network", "Server started on port 12345");

                // stna server
                Thread.sleep(500);

                chatClient = new ChatClient("Player_" + (int)(Math.random()*1000));
                if (chatClient.connect("localhost", 12345)) {
                    Gdx.app.log("Network", "Client connected successfully");
                } else {
                    Gdx.app.error("Network", "Client connection failed");
                }
            } catch (Exception e) {
                Gdx.app.error("Network", "Startup failed", e);
            }
        }).start();
    }


    public SpriteBatch getBatch() { return batch; }
    public ChatClient getChatClient() { return chatClient; }

    @Override
    public void dispose() {
        batch.dispose();
        if (chatClient != null) chatClient.disconnect();
        if (chatServer != null) chatServer.stop();
    }
    public static void main(String[] args) throws IOException {
        new ChatServer().start(12345);
    }
}
