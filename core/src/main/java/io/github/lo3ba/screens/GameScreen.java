package io.github.lo3ba.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.InputAdapter;
import io.github.lo3ba.Main;
import io.github.lo3ba.entities.Jet;

public class GameScreen implements Screen {
    private final Main game;
    private OrthographicCamera camera;
    private Jet jet;
    private Texture jetTexture;
    private Texture backgroundTexture;
    private BitmapFont font;
    private StringBuilder chatHistory;
    private boolean waitingForChatInput = false;

    public GameScreen(Main game) {
        this.game = game;
        Gdx.app.log("GameScreen", "GameScreen created");

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 600);
        this.font = new BitmapFont();
        this.chatHistory = new StringBuilder();

        loadAssets();
        setupChat();
    }

    private void loadAssets() {
        try {
            jetTexture = new Texture(Gdx.files.internal("jet.png"));
            backgroundTexture = new Texture(Gdx.files.internal("background.png"));

            jet = new Jet(jetTexture);
            jet.getSprite().setPosition(
                camera.viewportWidth/2 - jet.getSprite().getWidth()/2,
                50
            );

            Gdx.app.log("GameScreen", "Assets loaded successfully");
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Failed to load assets", e);
            // Création de textures de secours
            Pixmap jetPixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
            jetPixmap.setColor(Color.RED);
            jetPixmap.fill();
            jetTexture = new Texture(jetPixmap);

            Pixmap bgPixmap = new Pixmap(800, 600, Pixmap.Format.RGBA8888);
            bgPixmap.setColor(Color.DARK_GRAY);
            bgPixmap.fill();
            backgroundTexture = new Texture(bgPixmap);
        }
    }

    private void setupChat() {
        if (game.getChatClient() != null) {
            game.getChatClient().setOnMessageReceivedListener(this::appendMessage);
        }
    }

    private void appendMessage(String message) {
        Gdx.app.log("CHAT", "Received: " + message);
        chatHistory.append(message).append("\n");
        if (chatHistory.length() > 500) {
            chatHistory.delete(0, chatHistory.indexOf("\n") + 1);
        }
    }


    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);


        game.getBatch().begin();


        game.getBatch().draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);


        jet.draw(game.getBatch());


        font.draw(game.getBatch(), "Contrôles: [FLÈCHES] Bouger | [ESPACE] Tirer | [T] Chat", 10, 30);
        font.draw(game.getBatch(), chatHistory.toString(), 10, camera.viewportHeight - 20);

        if (waitingForChatInput) {
            font.draw(game.getBatch(), "En train d'écrire...", 10, 60);
        }

        game.getBatch().end();


        handleInput();
        jet.update(delta);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.T) && !waitingForChatInput) {
            waitingForChatInput = true;


            new Thread(() -> {
                String text = javax.swing.JOptionPane.showInputDialog(null, "Entrez votre message :", "Chat", javax.swing.JOptionPane.PLAIN_MESSAGE);
                if (text != null && !text.trim().isEmpty()) {
                    Gdx.app.postRunnable(() -> {
                        if (game.getChatClient() != null && game.getChatClient().isConnected()) {
                            game.getChatClient().sendMessage(text);
                            appendMessage("You: " + text);
                        }
                        waitingForChatInput = false;
                    });
                } else {
                    Gdx.app.postRunnable(() -> waitingForChatInput = false);
                }
            }).start();
        }
    }


    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose() {
        jetTexture.dispose();
        backgroundTexture.dispose();
        font.dispose();
    }


    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
