package io.github.lo3ba.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.lo3ba.DAO.Player;
import io.github.lo3ba.Main_Game;

import java.util.List;

public class MenuScreen implements Screen {
    private final Main_Game game;
    private Stage stage;
    private Skin skin;
    private Image jetPreview;
    private String selectedJet = "jet1";
    private final Texture jet1Texture, jet2Texture, jet3Texture;
    private SelectBox<String> nameSelect;
    private TextField nameField;

    public MenuScreen(Main_Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Load jet images
        jet1Texture = new Texture(Gdx.files.internal("tyaranadia2.png"));
        jet2Texture = new Texture(Gdx.files.internal("jet2.png"));
        jet3Texture = new Texture(Gdx.files.internal("jet3.png"));

        createMenu();
    }

    private void createMenu() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Top Players Table
        createTopPlayersTable();

        // Title
        table.add(new Label("Settings Menu", skin)).colspan(2).pad(10);
        table.row();

        // Player Selection
        createPlayerSelection(table);

        // Difficulty and Jet Selection
        createSettingsTable(table);

        // Start Button
        createStartButton(table);

        // Exit Button
        createExitButton(table);
    }

    private void createTopPlayersTable() {
        Table topRightTable = new Table();
        topRightTable.setPosition(Gdx.graphics.getWidth() - 75, Gdx.graphics.getHeight() - 75);
        stage.addActor(topRightTable);

        Label topPlayersLabel = new Label("Top 3 Players", skin);
        topRightTable.add(topPlayersLabel).pad(10);
        topRightTable.row();

        // Load top players
        List<Player> topPlayers = new Player().getTop3Players();
        if (topPlayers.isEmpty()) {
            topRightTable.add(new Label("No players found", skin)).pad(5);
            topRightTable.row();
        } else {
            for (Player player : topPlayers) {
                String playerInfo = player.getName() + " - " + player.getBestScore();
                Label playerLabel = new Label(playerInfo, skin);
                topRightTable.add(playerLabel).pad(5);
                topRightTable.row();
            }
        }
    }

    private void createPlayerSelection(Table table) {
        // Existing Players Dropdown
        nameSelect = new SelectBox<>(skin);
        Array<String> playerNames = new Player().getAllPlayerNames();
        nameSelect.setItems(playerNames);

        table.add(new Label("Select existing players:", skin)).pad(10);
        table.add(nameSelect).pad(10).width(200);
        table.row();

        // New Player Input
        nameField = new TextField("", skin);
        table.add(new Label("Enter new player:", skin)).pad(10);
        table.add(nameField).pad(10).width(200);
        table.row();

        // Add Player Button
        TextButton addButton = new TextButton("Ajouter Joueur", skin);
        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = nameField.getText().trim();
                if (!name.isEmpty()) {
                    new Player().addPlayer(name);
                    nameField.setText("");
                    // Refresh player list
                    nameSelect.setItems(new Player().getAllPlayerNames());
                }
            }
        });
        table.add(addButton).colspan(2).pad(20).center();
        table.row();
    }

    private void createSettingsTable(Table table) {
        Table settingsTable = new Table();
        table.add(settingsTable).colspan(2).pad(10);
        table.row();

        // Left Table (Selections)
        Table leftTable = new Table();

        // Difficulty Selection
        SelectBox<String> difficultySelect = new SelectBox<>(skin);
        difficultySelect.setItems("easy", "normal", "hard");
        leftTable.add(new Label("Select Difficulty:", skin)).pad(10).left();
        leftTable.row();
        leftTable.add(difficultySelect).pad(10).width(200);
        leftTable.row();

        // Jet Selection
        SelectBox<String> jetSelect = new SelectBox<>(skin);
        jetSelect.setItems("jet1", "jet2", "jet3");
        leftTable.add(new Label("Select Jet:", skin)).pad(10).left();
        leftTable.row();
        leftTable.add(jetSelect).pad(10).width(200);
        leftTable.row();

        // Jet Preview
        jetPreview = new Image(new TextureRegionDrawable(new TextureRegion(jet1Texture)));
        leftTable.add(jetPreview).pad(1).width(100).height(100);
        leftTable.row();
        jetSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedJet = jetSelect.getSelected();
                updateJetPreview();
            }
        });

        // Right Table (Sliders)
        Table rightTable = new Table();
        createSliders(rightTable);

        settingsTable.add(leftTable).pad(10).top();
        settingsTable.add(rightTable).pad(10).top();
    }

    private void createSliders(Table table) {
        Slider speedSlider = new Slider(1, 100, 1, false, skin);
        Slider attackSlider = new Slider(1, 100, 1, false, skin);
        Slider lifeSlider = new Slider(1, 100, 1, false, skin);

        table.add(new Label("Speed:", skin)).pad(10).left();
        table.row();
        table.add(speedSlider).pad(1).width(200);
        table.row();

        table.add(new Label("Attack:", skin)).pad(10).left();
        table.row();
        table.add(attackSlider).pad(1).width(200);
        table.row();

        table.add(new Label("Life:", skin)).pad(10).left();
        table.row();
        table.add(lifeSlider).pad(1).width(200);
    }

    private void createStartButton(Table table) {
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String playerName = nameSelect.getSelected();
                if (playerName == null || playerName.isEmpty()) {
                    playerName = nameField.getText().trim();
                    if (playerName.isEmpty()) {
                        playerName = "Guest";
                    }
                }

                String difficulty = "normal"; // Default if not using difficulty select
                game.setScreen(new GameScreen(game, playerName, difficulty, selectedJet));
            }
        });
        table.add(startButton).colspan(2).center();
        table.row();
    }

    private void createExitButton(Table table) {
        TextButton exitButton = new TextButton("Quit Game", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(exitButton).colspan(2).pad(20).center();
    }

    private void updateJetPreview() {
        TextureRegionDrawable drawable;
        switch (selectedJet) {
            case "jet2":
                drawable = new TextureRegionDrawable(new TextureRegion(jet2Texture));
                break;
            case "jet3":
                drawable = new TextureRegionDrawable(new TextureRegion(jet3Texture));
                break;
            default:
                drawable = new TextureRegionDrawable(new TextureRegion(jet1Texture));
                break;
        }
        jetPreview.setDrawable(drawable);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        jet1Texture.dispose();
        jet2Texture.dispose();
        jet3Texture.dispose();
    }
}
