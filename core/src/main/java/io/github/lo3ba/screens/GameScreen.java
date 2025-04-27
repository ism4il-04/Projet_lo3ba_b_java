package io.github.lo3ba.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl3.audio.Wav;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.lo3ba.DAO.Matche;
import io.github.lo3ba.DAO.Player;
import io.github.lo3ba.Main_Game;
import io.github.lo3ba.entities.*;
import java.util.Iterator;

public class GameScreen implements Screen {
    private Wav.Music music;
    private final Main_Game game;
    private OrthographicCamera camera;
    private Jet jet;
    private Texture jetTexture;
    private Texture backgroundTexture;
    private BitmapFont font;
    private BitmapFont scoreFont;
    private StringBuilder chatHistory;
    private boolean waitingForChatInput = false;
    private Array<EnemyJet> enemies;
    private float enemySpawnTimer;
    private Array<Explosion> explosions;
    private Texture enemyTexture;
    private Texture enemyBulletTexture;
    private Texture explosionTexture;
    private Texture playerBulletTexture;
    private Texture healthIconTexture;
    private Array<PowerUp> powerUps;
    private Texture healthPowerTexture;
    private Texture shieldPowerTexture;
    private Texture rapidFirePowerTexture;
    private Texture spreadShotPowerTexture;
    private Texture bombPowerTexture;
    private int playerScore = 0;
    private boolean gameOver = false;
    private float gameOverTimer = 0;
    private final float gameOverDelay = 3f;
    private final int healthIconSize = 30;
    private final int healthIconPadding = 5;
    private boolean isPaused = false;
    private String difficulty;
    private String playerName;

    public GameScreen(Main_Game game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 600);
        this.font = new BitmapFont();
        this.scoreFont = new BitmapFont();
        this.chatHistory = new StringBuilder();
        this.powerUps = new Array<>();

        //loadAssets();
        setupChat();
    }

    public GameScreen(Main_Game game, String playername, String niveau, String selectedJet) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 600);
        this.font = new BitmapFont();
        this.scoreFont = new BitmapFont();
        this.chatHistory = new StringBuilder();
        this.powerUps = new Array<>();
        this.difficulty = niveau;
        this.playerName = playername;
        if (selectedJet.equals("jet1")){
            loadAssets("tyaranadia2.png");
        } else if (selectedJet.equals("jet2")){
            loadAssets("jet2.png");
        } else if (selectedJet.equals("jet3")){
            loadAssets("jet3.png");
        }
        setupChat();
    }

    private void loadAssets(String j) {
        try {
            jetTexture = new Texture(Gdx.files.internal(j));
            backgroundTexture = new Texture(Gdx.files.internal("fontdecran.png"));
            enemyTexture = new Texture(Gdx.files.internal("tyaranadia.png"));
            enemyBulletTexture = new Texture(Gdx.files.internal("enemy_bullet.png"));
            explosionTexture = new Texture(Gdx.files.internal("boom.png"));
            playerBulletTexture = new Texture(Gdx.files.internal("shot_1.png"));
            healthIconTexture = new Texture(Gdx.files.internal("health_icon.png"));
            healthPowerTexture = new Texture(Gdx.files.internal("HP_Bonus.png"));
            shieldPowerTexture = new Texture(Gdx.files.internal("Armor_Bonus.png"));
            rapidFirePowerTexture = new Texture(Gdx.files.internal("Damage_Bonus.png"));
            spreadShotPowerTexture = new Texture(Gdx.files.internal("Rockets_Bonus.png"));
            bombPowerTexture = new Texture(Gdx.files.internal("Enemy_Destroy_Bonus.png"));
            setTextureFilters();

            scoreFont.getData().setScale(1.5f);
            scoreFont.setColor(Color.YELLOW);

            jet = new Jet(jetTexture, playerBulletTexture);
            jet.getSprite().setPosition(
                camera.viewportWidth/2 - jet.getSprite().getWidth()/2,
                50
            );

        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Failed to load assets", e);
            createFallbackTextures();
        }

        Music music= Gdx.audio.newMusic(Gdx.files.internal("music.wav"));
        music.setVolume(0.5f);
        music.setLooping(true);
        music.play();

    }

    private void setTextureFilters() {
        jetTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        enemyTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        enemyBulletTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        explosionTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        playerBulletTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        healthIconTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        healthPowerTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        shieldPowerTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        rapidFirePowerTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        spreadShotPowerTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bombPowerTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    private void createFallbackTextures() {
        Pixmap jetPixmap = new Pixmap(60, 60, Pixmap.Format.RGBA8888);
        jetPixmap.setColor(Color.RED);
        jetPixmap.fillTriangle(30, 0, 0, 60, 60, 60);
        jetTexture = new Texture(jetPixmap);
        jetPixmap.dispose();

        Pixmap enemyPixmap = new Pixmap(60, 60, Pixmap.Format.RGBA8888);
        enemyPixmap.setColor(Color.BLUE);
        enemyPixmap.fillTriangle(30, 60, 0, 0, 60, 0);
        enemyTexture = new Texture(enemyPixmap);
        enemyPixmap.dispose();

        Pixmap bgPixmap = new Pixmap(800, 600, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(Color.DARK_GRAY);
        bgPixmap.fill();
        backgroundTexture = new Texture(bgPixmap);
        bgPixmap.dispose();

        Pixmap bulletPixmap = new Pixmap(10, 20, Pixmap.Format.RGBA8888);
        bulletPixmap.setColor(Color.YELLOW);
        bulletPixmap.fillRectangle(0, 0, 10, 20);
        playerBulletTexture = new Texture(bulletPixmap);
        bulletPixmap.dispose();

        Pixmap enemyBulletPixmap = new Pixmap(10, 20, Pixmap.Format.RGBA8888);
        enemyBulletPixmap.setColor(Color.RED);
        enemyBulletPixmap.fillRectangle(0, 0, 10, 20);
        enemyBulletTexture = new Texture(enemyBulletPixmap);
        enemyBulletPixmap.dispose();

        Pixmap explosionPixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
        explosionPixmap.setColor(Color.ORANGE);
        explosionPixmap.fillCircle(25, 25, 25);
        explosionTexture = new Texture(explosionPixmap);
        explosionPixmap.dispose();

        Pixmap healthPowerPixmap = new Pixmap(30, 30, Pixmap.Format.RGBA8888);
        healthPowerPixmap.setColor(Color.GREEN);
        healthPowerPixmap.fillCircle(15, 15, 15);
        healthPowerTexture = new Texture(healthPowerPixmap);
        healthPowerPixmap.dispose();

        Pixmap rapidFirePixmap = new Pixmap(30, 30, Pixmap.Format.RGBA8888);
        rapidFirePixmap.setColor(Color.YELLOW);
        rapidFirePixmap.fillCircle(15, 15, 15);
        rapidFirePowerTexture = new Texture(rapidFirePixmap);
        rapidFirePixmap.dispose();

        Pixmap spreadShotPixmap = new Pixmap(30, 30, Pixmap.Format.RGBA8888);
        spreadShotPixmap.setColor(Color.PURPLE);
        spreadShotPixmap.fillCircle(15, 15, 15);
        spreadShotPowerTexture = new Texture(spreadShotPixmap);
        spreadShotPixmap.dispose();

        Pixmap bombPixmap = new Pixmap(30, 30, Pixmap.Format.RGBA8888);
        bombPixmap.setColor(Color.RED);
        bombPixmap.fillCircle(15, 15, 15);
        bombPowerTexture = new Texture(bombPixmap);
        bombPixmap.dispose();

        Pixmap shieldPowerPixmap = new Pixmap(30, 30, Pixmap.Format.RGBA8888);
        shieldPowerPixmap.setColor(Color.BLUE);
        shieldPowerPixmap.fillCircle(15, 15, 15);
        shieldPowerTexture = new Texture(shieldPowerPixmap);
        shieldPowerPixmap.dispose();

        Pixmap healthPixmap = new Pixmap(healthIconSize, healthIconSize, Pixmap.Format.RGBA8888);
        healthPixmap.setColor(Color.GREEN);
        healthPixmap.fillTriangle(
            healthIconSize/2, 0,
            0, healthIconSize,
            healthIconSize, healthIconSize
        );
        healthIconTexture = new Texture(healthPixmap);
        healthPixmap.dispose();

        jet = new Jet(jetTexture, playerBulletTexture);
        jet.getSprite().setPosition(
            camera.viewportWidth/2 - jet.getSprite().getWidth()/2,
            50
        );

        Gdx.app.log("GameScreen", "Fallback textures created");
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
        handleInput();

        if (gameOver) {
            handleGameOver(delta);
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();
        game.getBatch().draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);

        jet.draw(game.getBatch());
        for (EnemyJet enemy : enemies) {
            enemy.render(game.getBatch());
        }
        for (Explosion explosion : explosions) {
            explosion.render(game.getBatch());
        }
        for (PowerUp powerUp : powerUps) {
            powerUp.draw(game.getBatch());
        }

        for (int i = 0; i < jet.getHealth(); i++) {
            game.getBatch().draw(healthIconTexture,
                10 + i * (healthIconSize + healthIconPadding),
                10,
                healthIconSize,
                healthIconSize);
        }

        scoreFont.draw(game.getBatch(),
            "Score: " + playerScore,
            camera.viewportWidth - 150,
            camera.viewportHeight - 30);

        font.draw(game.getBatch(), chatHistory.toString(), 10, camera.viewportHeight - 20);

        if (waitingForChatInput) {
            font.draw(game.getBatch(), "En train d'écrire...", 10, 60);
        }

        if (isPaused) {
            font.getData().setScale(2.0f);
            font.setColor(Color.YELLOW);
            font.draw(game.getBatch(), "PAUSE",
                camera.viewportWidth / 2 - 50,
                camera.viewportHeight / 2 + 20);
            font.getData().setScale(1.0f);
            font.setColor(Color.WHITE);
        }

        game.getBatch().end();

        if (!isPaused && !gameOver) {
            jet.update(delta);
            updateEnemies(delta);
            updatePowerUps(delta);
            checkCollisions();
            updateExplosions(delta);
            checkGameOver();
        }
    }

    public void spawnPowerUp(PowerUp.Type type, float x, float y) {
        Texture texture = null;
        switch (type) {
            case HEALTH: texture = healthPowerTexture; break;
            case SHIELD: texture = shieldPowerTexture; break;
            case RAPID_FIRE: texture = rapidFirePowerTexture; break;
            case SPREAD_SHOT: texture = spreadShotPowerTexture; break;
            case BOMB: texture = bombPowerTexture; break;
        }

        if (texture != null) {
            powerUps.add(new PowerUp(texture, type, x, y));
        }
    }

    private void updatePowerUps(float delta) {
        for (Iterator<PowerUp> iterator = powerUps.iterator(); iterator.hasNext();) {
            PowerUp powerUp = iterator.next();
            powerUp.update(delta);

            if (powerUp.getBounds().overlaps(jet.getSprite().getBoundingRectangle())) {
                jet.applyPowerUp(powerUp.getType());

                if (powerUp.getType() == PowerUp.Type.BOMB) {
                    for (EnemyJet enemy : enemies) {
                        explosions.add(new Explosion(explosionTexture, enemy.getBounds().x, enemy.getBounds().y));
                        playerScore += 100;
                    }
                    enemies.clear();
                }

                iterator.remove();
            } else if (powerUp.getY() < -30 || !powerUp.isActive()) {
                iterator.remove();
            }
        }
    }

    private void handleGameOver(float delta) {
        gameOverTimer += delta;
        if (gameOverTimer >= gameOverDelay) {
            Matche matche = new Matche();
            matche.AjouterMatche(playerName,playerScore,difficulty);
            Player p=new Player();
            p.majScoreSiDepasse(playerName,playerScore);
            game.setScreen(new MenuScreen(game));
        }

        game.getBatch().begin();
        font.draw(game.getBatch(), "GAME OVER", camera.viewportWidth/2 - 50, camera.viewportHeight/2);
        scoreFont.draw(game.getBatch(), "Score Final: " + playerScore, camera.viewportWidth/2 - 70, camera.viewportHeight/2 - 40);
        game.getBatch().end();
    }

    private void updateEnemies(float delta) {
        enemySpawnTimer += delta;
        if (enemySpawnTimer >= 1.5f) {
            enemies.add(new EnemyJet(enemyTexture, enemyBulletTexture, jet,this));
            enemySpawnTimer = 0;
        }

        for (Iterator<EnemyJet> iterator = enemies.iterator(); iterator.hasNext();) {
            EnemyJet enemy = iterator.next();
            enemy.update(delta);

            if (enemy.isOffScreen() || !enemy.isAlive()) {
                iterator.remove();
            }
        }
    }

    private void checkCollisions() {
        for (Iterator<Bullet> bulletIterator = jet.getBullets().iterator(); bulletIterator.hasNext();) {
            Bullet bullet = bulletIterator.next();

            for (Iterator<EnemyJet> enemyIterator = enemies.iterator(); enemyIterator.hasNext();) {
                EnemyJet enemy = enemyIterator.next();

                if (bullet.getBounds().overlaps(enemy.getBounds())) {
                    explosions.add(new Explosion(explosionTexture, enemy.getBounds().x, enemy.getBounds().y));
                    bulletIterator.remove();
                    enemy.die();
                    enemyIterator.remove();
                    playerScore += 100;
                    break;
                }
            }
        }

        for (EnemyJet enemy : enemies) {
            for (Iterator<DirectedBullet> bulletIterator = enemy.getBullets().iterator(); bulletIterator.hasNext();) {
                DirectedBullet bullet = bulletIterator.next();

                 if (bullet.getBounds().overlaps(jet.getSprite().getBoundingRectangle())) {
                    jet.takeDamage(1);
                    bulletIterator.remove();

                    if (jet.getHealth() <= 0) {
                        gameOver = true;
                    }
                    break;
                }
            }
        }
    }

    private void updateExplosions(float delta) {
        for (Iterator<Explosion> iterator = explosions.iterator(); iterator.hasNext();) {
            Explosion explosion = iterator.next();
            explosion.update(delta);

            if (explosion.isExpired()) {
                iterator.remove();
            }
        }
    }

    private void checkGameOver() {
        if (jet.getHealth() <= 0) {
            gameOver = true;
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            isPaused = !isPaused;
            Gdx.app.log("GameScreen", "Game " + (isPaused ? "paused" : "resumed"));
        }

        if (isPaused) {
            return;
        }

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
        music.dispose();
        jetTexture.dispose();
        backgroundTexture.dispose();
        enemyTexture.dispose();
        enemyBulletTexture.dispose();
        explosionTexture.dispose();
        playerBulletTexture.dispose();
        healthIconTexture.dispose();
        font.dispose();
        scoreFont.dispose();
        healthPowerTexture.dispose();
        shieldPowerTexture.dispose();
        rapidFirePowerTexture.dispose();
        spreadShotPowerTexture.dispose();
        bombPowerTexture.dispose();
        for (EnemyJet enemy : enemies) {
            enemy.dispose();
        }
        explosions.clear();

        for (PowerUp powerUp : powerUps) {
            powerUp.getTexture().dispose();
        }
        powerUps.clear();
    }

    @Override
    public void show() {
        enemies = new Array<>();
        enemySpawnTimer = 0;
        explosions = new Array<>();
        powerUps = new Array<>();
        playerScore = 0;
        gameOver = false;
        gameOverTimer = 0;
        isPaused = false;
    }

    @Override
    public void pause() {
        isPaused = true;
        Gdx.app.log("GameScreen", "Game paused (app focus lost)");
    }

    @Override
    public void resume() {
        isPaused = false;
        Gdx.app.log("GameScreen", "Game resumed (app focus regained)");
    }

    @Override
    public void hide() {
    }
}
