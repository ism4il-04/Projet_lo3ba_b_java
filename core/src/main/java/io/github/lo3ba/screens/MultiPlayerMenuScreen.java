package io.github.lo3ba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.lo3ba.Main_Game;
import io.github.lo3ba.multiplayer.Client;
import io.github.lo3ba.multiplayer.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiPlayerMenuScreen implements Screen {
    private Main_Game game;
    private Stage stage;
    private Skin skin;

    private Client client;
    private Server server;

    private TextField nameField;
    private TextField portField;
    private SelectBox<String> shipSelector;

    private TextButton createButton;
    private TextButton joinButton;

    public MultiPlayerMenuScreen(Main_Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        setupUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);

        nameField = new TextField("Player", skin);
        portField = new TextField("1234", skin);
        shipSelector = new SelectBox<>(skin);
        shipSelector.setItems("Jet1", "Jet2", "Jet3");

        createButton = new TextButton("Create Game", skin);
        joinButton = new TextButton("Join Game", skin);

        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new Thread(() -> {
                    try {
                        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(portField.getText()));
                        Server server = new Server(serverSocket);
                        server.startServer();
                        Socket socket = new Socket("localhost", Integer.parseInt(portField.getText()));
                        Gdx.app.postRunnable(() -> {
                            game.setScreen(new MultiplayerScreen(game, new Client(socket, nameField.getText())));
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }); // start server
        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new Thread(() -> {
                    try {
                        Socket socket = new Socket("localhost", Integer.parseInt(portField.getText()));
                        Gdx.app.postRunnable(() -> {
                            game.setScreen(new MultiplayerScreen(game, new Client(socket, nameField.getText())));
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });

        table.add("Name").left(); table.add(nameField).row();
        table.add("Port").left(); table.add(portField).row();
        table.add("Ship").left(); table.add(shipSelector).row();
        table.add(createButton).padTop(20).colspan(2).row();
        table.add(joinButton).padTop(10).colspan(2).row();

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(v);
        stage.draw();

    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        stage.dispose();

    }

}
