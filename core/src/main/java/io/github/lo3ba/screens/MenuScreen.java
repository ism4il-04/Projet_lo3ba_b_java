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
import io.github.lo3ba.DAO.ConnexionBD;
import io.github.lo3ba.DAO.Player;
import io.github.lo3ba.Main_Game;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MenuScreen implements Screen {
    private final Main_Game game;
    private Stage stage;
    private Skin skin;
    private Image jetPreview;
    private String selectedJet = "Basic Jet";
    private String selectedLevel = "easy";
    private ProgressBar speedBar, attackBar;
    private Label jetNameLabel, jetUnlockLabel, levelUnlockLabel;
    private int playerHighScore = 0;
    private TextField newPlayerField;
    private TextButton createPlayerButton;
    private boolean showNewPlayerField = false;
    private SelectBox<String> playerSelect, levelSelect, jetSelect;

    public MenuScreen(Main_Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        createMenu();
    }

    private void createMenu() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Table topRightTable = new Table();
        topRightTable.setPosition(Gdx.graphics.getWidth() - 75, Gdx.graphics.getHeight() - 75);
        stage.addActor(topRightTable);

        Label topPlayersLabel = new Label("Top 3 Players", skin);
        topRightTable.add(topPlayersLabel).pad(10);
        topRightTable.row();

        Player temp = new Player();
        List<Player> topPlayers = temp.getTop3Players();
        for (Player player : topPlayers) {
            String playerInfo = player.getName() + " - " + player.getMeilleurScore();
            Label playerLabel = new Label(playerInfo, skin);
            topRightTable.add(playerLabel).pad(5);
            topRightTable.row();
        }

        Table contentTable = new Table();
        mainTable.add(contentTable).expand().fill().pad(20);

        Label titleLabel = new Label("Game Menu", skin);
        titleLabel.setFontScale(1.5f);
        contentTable.add(titleLabel).colspan(2).padBottom(30);
        contentTable.row();

        // Player Selection
        Array<String> playerOptions = new Array<>();
        playerOptions.addAll(temp.getAllPlayersNames());
        playerOptions.add("Create New Player");

        playerSelect = new SelectBox<>(skin);
        playerSelect.setItems(playerOptions);

        contentTable.add(new Label("Select Player:", skin)).padRight(10).left();
        contentTable.add(playerSelect).width(250).padBottom(15);
        contentTable.row();

        // New Player Field and Create Button
        Table newPlayerTable = new Table();
        newPlayerField = new TextField("", skin);
        newPlayerField.setVisible(false);
        createPlayerButton = new TextButton("Create", skin);
        createPlayerButton.setVisible(false);
        newPlayerTable.add(newPlayerField).width(200).padRight(10);
        newPlayerTable.add(createPlayerButton).width(100);

        contentTable.add(newPlayerTable).colspan(2).padBottom(15);
        contentTable.row();

        // Level Selection
        levelSelect = new SelectBox<>(skin);
        Array<String> availableLevels = getAvailableLevels(0);
        levelSelect.setItems(availableLevels);

        contentTable.add(new Label("Select Level:", skin)).padRight(10).left();
        contentTable.add(levelSelect).width(250).padBottom(15);
        contentTable.row();

        levelUnlockLabel = new Label("", skin);
        contentTable.add(levelUnlockLabel).colspan(2).padBottom(15);
        contentTable.row();

        Table jetTable = new Table();
        Table jetPreviewColumn = new Table();
        jetPreview = new Image();
        jetPreviewColumn.add(jetPreview).size(150).pad(10);
        jetPreviewColumn.row();
        jetNameLabel = new Label("", skin);
        jetPreviewColumn.add(jetNameLabel);
        jetPreviewColumn.row();
        jetUnlockLabel = new Label("", skin);
        jetPreviewColumn.add(jetUnlockLabel);

        Table jetStatsColumn = new Table();
        jetStatsColumn.add(new Label("Jet Stats", skin)).colspan(2).padBottom(10);
        jetStatsColumn.row();

        jetStatsColumn.add(new Label("Speed:", skin)).padRight(10).left();
        speedBar = new ProgressBar(0, 100, 1, false, skin);
        jetStatsColumn.add(speedBar).width(150).padBottom(5);
        jetStatsColumn.row();

        jetStatsColumn.add(new Label("Attack:", skin)).padRight(10).left();
        attackBar = new ProgressBar(0, 100, 1, false, skin);
        jetStatsColumn.add(attackBar).width(150);

        jetTable.add(jetPreviewColumn).padRight(20);
        jetTable.add(jetStatsColumn);

        contentTable.add(jetTable).colspan(2).padBottom(30);
        contentTable.row();

        // Jet Selection
        jetSelect = new SelectBox<>(skin);
        Array<String> availableJets = getAvailableJets(0);
        jetSelect.setItems(availableJets);

        contentTable.add(new Label("Select Jet:", skin)).padRight(10).left();
        contentTable.add(jetSelect).width(250).padBottom(30);
        contentTable.row();

        Table buttonTable = new Table();
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String playerName = playerSelect.getSelected();
                if ("Create New Player".equals(playerName)) {
                    playerName = newPlayerField.getText();
                    if (playerName.isEmpty()) return;
                }

                if (playerName != null && !playerName.isEmpty()) {
                    game.setScreen(new GameScreen(game, playerName,
                            levelSelect.getSelected(), jetSelect.getSelected()));
                }
            }
        });
        buttonTable.add(startButton).padRight(20).width(150);

        TextButton exitButton = new TextButton("Exit Game", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        buttonTable.add(exitButton).width(150);

        contentTable.add(buttonTable).colspan(2).padTop(20);

        // Listeners
        playerSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selection = playerSelect.getSelected();
                showNewPlayerField = "Create New Player".equals(selection);
                newPlayerField.setVisible(showNewPlayerField);
                createPlayerButton.setVisible(showNewPlayerField);

                if (!showNewPlayerField && selection != null && !selection.isEmpty()) {
                    playerHighScore = getPlayerHighScore(selection);

                    Array<String> levels = getAvailableLevels(playerHighScore);
                    levelSelect.setItems(levels);
                    if (levels.size > 0) {
                        levelSelect.setSelectedIndex(0);
                        updateLevelInfo(levelSelect.getSelected());
                    }

                    Array<String> jets = getAvailableJets(playerHighScore);
                    jetSelect.setItems(jets);
                    if (jets.size > 0) {
                        jetSelect.setSelectedIndex(0);
                        updateJetInfo(jetSelect.getSelected());
                    }
                }
            }
        });

        createPlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String newPlayerName = newPlayerField.getText();
                if (!newPlayerName.isEmpty()) {
                    Player newPlayer = new Player();
                    newPlayer.Ajouter(newPlayerName);
                    playerSelect.getItems().insert(playerSelect.getItems().size - 1, newPlayerName);
                    playerSelect.setSelected(newPlayerName);
                    newPlayerField.setVisible(false);
                    createPlayerButton.setVisible(false);
                }
            }
        });

        levelSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedLevel = levelSelect.getSelected();
                updateLevelInfo(selectedLevel);
            }
        });

        jetSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedJet = jetSelect.getSelected();
                updateJetInfo(selectedJet);
            }
        });

        if (availableLevels.size > 0) {
            levelSelect.setSelectedIndex(0);
            updateLevelInfo(levelSelect.getSelected());
        }
        if (availableJets.size > 0) {
            jetSelect.setSelectedIndex(0);
            updateJetInfo(jetSelect.getSelected());
        }
    }

    private Array<String> getAvailableLevels(int playerScore) {
        Array<String> levels = new Array<>();
        if (playerScore >= 0) levels.add("easy");
        if (playerScore >= 1000) levels.add("normal");
        if (playerScore >= 3000) levels.add("hard");
        return levels;
    }

    private Array<String> getAvailableJets(int playerScore) {
        Array<String> jets = new Array<>();
        try {
            Statement stm = ConnexionBD.seConnecter();
            ResultSet rs = stm.executeQuery("SELECT name FROM Jet WHERE unlock_score <= " + playerScore);
            while (rs.next()) {
                jets.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jets;
    }

    private int getPlayerHighScore(String playerName) {
        try {
            Statement stm = ConnexionBD.seConnecter();
            ResultSet rs = stm.executeQuery("SELECT meilleurScore FROM Player WHERE nom = '" + playerName + "'");
            if (rs.next()) {
                return rs.getInt("meilleurScore");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void updateLevelInfo(String levelName) {
        int requiredScore = 0;
        switch (levelName) {
            case "normal": requiredScore = 1000; break;
            case "hard": requiredScore = 3000; break;
        }

        if (playerHighScore < requiredScore) {
            levelUnlockLabel.setText("Need " + (requiredScore - playerHighScore) + " points to unlock");
            levelUnlockLabel.setColor(Color.RED);
        } else {
            levelUnlockLabel.setText("Unlocked!");
            levelUnlockLabel.setColor(Color.GREEN);
        }
    }

    private void updateJetInfo(String jetName) {
        try {
            Statement stm = ConnexionBD.seConnecter();
            ResultSet rs = stm.executeQuery("SELECT * FROM Jet WHERE name = '" + jetName + "'");
            if (rs.next()) {
                String texturePath = rs.getString("texture_path");
                Texture texture = new Texture(Gdx.files.internal(texturePath));
                jetPreview.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));

                int speed = rs.getInt("speed");
                int attack = rs.getInt("attack");
                int unlockScore = rs.getInt("unlock_score");

                speedBar.setValue(speed);
                attackBar.setValue(attack);
                jetNameLabel.setText(jetName);

                if (playerHighScore < unlockScore) {
                    jetUnlockLabel.setText("Need " + (unlockScore - playerHighScore) + " points");
                    jetUnlockLabel.setColor(Color.RED);
                } else {
                    jetUnlockLabel.setText("Unlocked!");
                    jetUnlockLabel.setColor(Color.GREEN);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    }
}
