package io.github.lo3ba.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.lo3ba.Main_Game;

public class MainMenuScreen implements Screen {

    private final Main_Game game;
    private Stage stage;//pour manager les UI
    private Texture background;//texture de l'image background

    public MainMenuScreen(Main_Game game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport()); //initialisé avec ScreenViewport pour gérer l’adaptation à la taille de l’écran.
        Gdx.input.setInputProcessor(stage);//dit à LibGDX que tous les événements d’entrée (clavier, souris, etc.) vont au stage

        background = new Texture("menu_background.png");

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Start button
        TextButton startButton = new TextButton("Start Soloplayer", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game)); // Start game
            }
        });

        //Multiplayer button
        TextButton multiplayerButton = new TextButton("Start Multiplayer", skin);
        multiplayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MultiplayerLoaderScreen(game,"Ismail")); // Start game
            }
        });

        // Exit button
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit(); // Exit app
            }
        });

        table.add(startButton).width(150).height(40).pad(20).row();
        table.add(multiplayerButton).width(150).height(40).pad(20).row();
        table.add(exitButton).width(150).height(40).pad(20);


        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();
        game.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().end();

        stage.act(delta); //dessine tous les widgets (boutons, textes, etc.).
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        background.dispose();
    }
}
