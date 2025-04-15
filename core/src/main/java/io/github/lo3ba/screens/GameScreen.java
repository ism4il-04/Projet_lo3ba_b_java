package io.github.lo3ba.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.lo3ba.Main_Game;
import io.github.lo3ba.entities.*;
import java.util.Iterator;

public class GameScreen implements Screen {
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
    private int playerScore = 0;
    private boolean gameOver = false;
    private float gameOverTimer = 0;
    private final float gameOverDelay = 3f;
    private final int healthIconSize = 30;
    private final int healthIconPadding = 5;

    public GameScreen(Main_Game game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 600);
        this.font = new BitmapFont();
        this.scoreFont = new BitmapFont();
        this.chatHistory = new StringBuilder();

        loadAssets();
        setupChat();
    }

    private void loadAssets() {
        try {
            jetTexture = new Texture(Gdx.files.internal("jet.png"));
            backgroundTexture = new Texture(Gdx.files.internal("background.png"));
            enemyTexture = new Texture(Gdx.files.internal("enemy_jet.png"));
            enemyBulletTexture = new Texture(Gdx.files.internal("enemy_bullet.png"));
            explosionTexture = new Texture(Gdx.files.internal("explosion.png"));
            playerBulletTexture = new Texture(Gdx.files.internal("shot_1.png"));
            healthIconTexture = new Texture(Gdx.files.internal("health_icon.png"));

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
    }

    private void setTextureFilters() {
        jetTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        enemyTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        enemyBulletTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        explosionTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        playerBulletTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        healthIconTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }
    private void createFallbackTextures() {//bach la 9adara lah w matloadawch l assets nqdiw gharad b hado

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

        // Draw game elements
        jet.draw(game.getBatch());
        for (EnemyJet enemy : enemies) {
            enemy.render(game.getBatch());
        }
        for (Explosion explosion : explosions) {
            explosion.render(game.getBatch());
        }

        //  health icons
        for (int i = 0; i < jet.getHealth(); i++) {
            game.getBatch().draw(healthIconTexture,
                10 + i * (healthIconSize + healthIconPadding),
                10,
                healthIconSize,
                healthIconSize);
        }

        // score
        scoreFont.draw(game.getBatch(),
            "Score: " + playerScore,
            camera.viewportWidth - 150,
            camera.viewportHeight - 30);


      //  font.draw(game.getBatch(), "Contrôles: [FLÈCHES] Bouger | [ESPACE] Tirer | [T] Chat", 10, 30); (hit katgheti douk les icones lte7t hta nchouf fin n7toha)
        font.draw(game.getBatch(), chatHistory.toString(), 10, camera.viewportHeight - 20);

        if (waitingForChatInput) {
            font.draw(game.getBatch(), "En train d'écrire...", 10, 60);
        }

        game.getBatch().end();


        if (!gameOver) {
            handleInput();
            jet.update(delta);
            updateEnemies(delta);
            checkCollisions();
            updateExplosions(delta);
            checkGameOver();
        }
    }

    private void handleGameOver(float delta) {
        gameOverTimer += delta;
        if (gameOverTimer >= gameOverDelay) {
            game.setScreen(new GameScreen(game));
        }

        game.getBatch().begin();
        font.draw(game.getBatch(), "GAME OVER", camera.viewportWidth/2 - 50, camera.viewportHeight/2);
        scoreFont.draw(game.getBatch(), "Score Final: " + playerScore, camera.viewportWidth/2 - 70, camera.viewportHeight/2 - 40);
        game.getBatch().end();
    }

    private void updateEnemies(float delta) {
        enemySpawnTimer += delta;
        if (enemySpawnTimer >= 1.5f) {
            enemies.add(new EnemyJet(enemyTexture, enemyBulletTexture, jet));
            enemySpawnTimer = 0;
        }

        for (Iterator<EnemyJet> iterator = enemies.iterator(); iterator.hasNext();) {
            EnemyJet enemy = iterator.next();
            enemy.update(delta);

            if (enemy.isOffScreen()) {
                iterator.remove();
            }
        }
    }

    private void checkCollisions() {
        // Player bullets vs enemies
        for (Iterator<Bullet> bulletIterator = jet.getBullets().iterator(); bulletIterator.hasNext();) {
            Bullet bullet = bulletIterator.next();

            for (Iterator<EnemyJet> enemyIterator = enemies.iterator(); enemyIterator.hasNext();) {
                EnemyJet enemy = enemyIterator.next();

                if (bullet.getBounds().overlaps(enemy.getBounds())) {
                    explosions.add(new Explosion(explosionTexture, enemy.getBounds().x, enemy.getBounds().y));
                    bulletIterator.remove();
                    enemyIterator.remove();
                    playerScore += 100;
                    break;
                }
            }
        }

        // Enemy bullets vs player
        for (EnemyJet enemy : enemies) {
            for (Iterator<DirectedBullet> bulletIterator = enemy.getBullets().iterator(); bulletIterator.hasNext();) {
                DirectedBullet bullet = bulletIterator.next();

                if (bullet.getBounds().overlaps(jet.getSprite().getBoundingRectangle())) {
                    jet.takeDamage(1); // we lose 1 life
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
        enemyTexture.dispose();
        enemyBulletTexture.dispose();
        explosionTexture.dispose();
        playerBulletTexture.dispose();
        healthIconTexture.dispose();
        font.dispose();
        scoreFont.dispose();

        for (EnemyJet enemy : enemies) {
            enemy.dispose();
        }
        explosions.clear();
    }

    @Override
    public void show() {
        enemies = new Array<>();
        enemySpawnTimer = 0;
        explosions = new Array<>();
        playerScore = 0;
        gameOver = false;
        gameOverTimer = 0;
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
