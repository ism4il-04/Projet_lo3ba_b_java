package io.github.lo3ba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.lo3ba.DAO.Player;
import io.github.lo3ba.Main_Game;
import io.github.lo3ba.multiplayer.Client;
import io.github.lo3ba.multiplayer.ClientHandler;
import io.github.lo3ba.multiplayer.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiplayerLoaderScreen extends InputAdapter implements Screen {
    private final Main_Game game;
    private Stage stage;
    private Skin skin;
    private TextField hostField;
    private TextField portField;

    public enum Mode {
        HOST, JOIN
    }

    public MultiplayerLoaderScreen(Main_Game game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        setupUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);

        Label title = new Label("Multiplayer Setup", skin);
        title.setFontScale(1.8f);

        hostField = new TextField("localhost", skin);
        portField = new TextField("1234", skin);

        TextButton hostButton = new TextButton("Host Game", skin);
        TextButton joinButton = new TextButton("Join Game", skin);
        Player temp= new Player();
        final SelectBox<String> nameSelect = new SelectBox<>(skin);
        nameSelect.setItems(temp.getAllPlayersNames());
        hostButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    int port = Integer.parseInt(portField.getText());

                    // Lancer le serveur dynamiquement
                    new Thread(() -> {
                        try {
                            new Server().start(port);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();

                    // Connexion du client
                    Thread.sleep(300);
                    Socket socket = new Socket(hostField.getText(), port);
                    Client client = new Client(socket, nameSelect.getSelected() );
                    game.setScreen(new MultiplayerScreen(game, client));
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int port = Integer.parseInt(portField.getText());
                try {
                    Socket socket = new Socket(hostField.getText(), port);
                    Client client = new Client(socket, nameSelect.getSelected() );                    game.setScreen(new MultiplayerScreen(game,new Client(socket,nameSelect.getSelected() )));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        table.add(title).padBottom(30).colspan(2).row();
        table.add(new Label("Select existing players:", skin)).pad(10);
        table.add(nameSelect).pad(10).width(200);
        table.row();
        table.add(new Label("Host/IP:", skin)).left(); table.add(hostField).width(200).row();
        table.add(new Label("Port:", skin)).left(); table.add(portField).width(200).row();
        table.add(hostButton).padTop(30).colspan(2).width(200).height(50).row();
        table.add(joinButton).padTop(10).colspan(2).width(200).height(50).row();

        stage.addActor(table);
    }

    @Override public void show() {}
    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}

