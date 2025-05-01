package io.github.lo3ba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.lo3ba.Main_Game;

import java.util.Random;

public class GameOverScreen implements Screen {
    private final Main_Game game;
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private int finalScore;
    private int currentDisplayedScore = 0;
    private float animationTimer = 0;
    private Label scoreLabel;
    private Label highScoreLabel;
    private BitmapFont bloodyFont;
    private BitmapFont regularFont;
    private String difficulty;
    private String playerName;
    private String jetName;

    // Pour les particules de débris
    private Point[] debris;
    private Random random = new Random();
    private Color backgroundColor = new Color(0.1f, 0.1f, 0.1f, 1); // Gris foncé

    public GameOverScreen(Main_Game game, int finalScore,String playerName, String difficulty ,String jetName) {
        this.game = game;
        this.finalScore = finalScore;
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.jetName = jetName;

        // Initialisation du stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Initialisation du ShapeRenderer
        shapeRenderer = new ShapeRenderer();

        // Chargement des polices
        bloodyFont = createBloodyFont();
        regularFont = createRegularFont();

        // Génération des débris
        debris = generateDebris();

        // Création de l'interface
        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Label "GAME OVER"
        Label.LabelStyle bloodyStyle = new Label.LabelStyle(bloodyFont, Color.RED);
        Label gameOverLabel = new Label("GAME OVER", bloodyStyle);
        gameOverLabel.setAlignment(Align.center);

        // Label du score
        Label.LabelStyle scoreStyle = new Label.LabelStyle(regularFont, Color.WHITE);
        scoreLabel = new Label("Score: 0", scoreStyle);
        scoreLabel.setAlignment(Align.center);

        // Label du meilleur score
        Label.LabelStyle highScoreStyle = new Label.LabelStyle(regularFont, Color.YELLOW);
        highScoreLabel = new Label(getHighScoreText(finalScore), highScoreStyle);
        highScoreLabel.setAlignment(Align.center);

        // Style des boutons
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = regularFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.LIGHT_GRAY;

        // Bouton Rejouer
        TextButton restartButton = new TextButton("Rejouer", buttonStyle);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game,playerName,difficulty,jetName));
            }
        });

        //bouton menu
        TextButton menuButton = new TextButton("Menu", buttonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        // Bouton Quitter
        TextButton quitButton = new TextButton("Quitter", buttonStyle);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Organisation des éléments
        table.add(gameOverLabel).padBottom(30).row();
        table.add(scoreLabel).padBottom(20).row();
        table.add(highScoreLabel).padBottom(30).row();

        Table buttonTable = new Table();
        buttonTable.add(restartButton).padRight(20);
        buttonTable.add(menuButton).padRight(20);
        buttonTable.add(quitButton).padLeft(20);


        table.add(buttonTable);
        stage.addActor(table);
    }

    private BitmapFont createBloodyFont() {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(3f);
        font.setColor(Color.RED);
        // Configuration du filtre de texture
        font.getRegion().getTexture().setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );
        return font;
    }

    private BitmapFont createRegularFont() {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        return font;
    }

    private String getHighScoreText(int score) {
        Preferences prefs = Gdx.app.getPreferences("GamePreferences");
        int highScore = prefs.getInteger("highScore", 0);

        if (score > highScore) {
            prefs.putInteger("highScore", score);
            prefs.flush();
            return "Nouveau record: " + score + "!";
        }
        return "Meilleur score: " + highScore;
    }

    private Point[] generateDebris() {
        Point[] debris = new Point[50];
        for (int i = 0; i < debris.length; i++) {
            debris[i] = new Point(
                random.nextInt(Gdx.graphics.getWidth()),
                random.nextInt(Gdx.graphics.getHeight())
            );
        }
        return debris;
    }

    private void drawDebris() {
        shapeRenderer.setProjectionMatrix(stage.getViewport().getCamera().combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0.4f, 0.4f, 0.4f, 0.6f);

        for (Point p : debris) {
            shapeRenderer.circle(p.x, p.y, 4f);
        }

        shapeRenderer.end();
    }

    private void updateScoreAnimation(float delta) {
        if (currentDisplayedScore < finalScore) {
            int increment = Math.max(1, (finalScore - currentDisplayedScore) / 5);
            currentDisplayedScore += increment;
            if (currentDisplayedScore > finalScore) {
                currentDisplayedScore = finalScore;
            }
            scoreLabel.setText("Score: " + currentDisplayedScore);
        }
    }

    @Override
    public void render(float delta) {
        updateScoreAnimation(delta);
        animationTimer += delta;

        // Nettoyage de l'écran
        ScreenUtils.clear(backgroundColor);

        // Dessin des éléments
        drawDebris();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        debris = generateDebris();
    }

    @Override
    public void dispose() {
        stage.dispose();
        bloodyFont.dispose();
        regularFont.dispose();
        shapeRenderer.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    private static class Point {
        float x, y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
