package io.github.lo3ba.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.lo3ba.Main_Game;
import io.github.lo3ba.entities.Laser;
import io.github.lo3ba.entities.Ship;
import io.github.lo3ba.multiplayer.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class MultiplayerScreen extends InputAdapter implements Screen {
    private Main_Game main;
    private OrthographicCamera camera;
    private Viewport viewport;

    private float lastSentX = -1;
    private float lastSentY = -1;


    Client client;

    private SpriteBatch batch;
    private Texture background;
    private Texture playerShipTexture;
    private Texture enemyShipTexture;
    private Texture playerLaserTexture;
    private Texture enemyLaserTexture;

    private final int WORLD_WIDTH = 800;
    private final int WORLD_HEIGHT = 600;

    private Ship playerShip;
    private Ship enemyShip;

    private LinkedList<Laser> playerLasers;
    private LinkedList<Laser> enemyLasers;

    public MultiplayerScreen(Main_Game main, Client client) {
        this.main = main;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        this.client = client;
        client.setMultiplayerScreen(this);

        System.out.println("loading assets");
        background = new Texture(Gdx.files.internal("fontdecran.png"));

        playerShipTexture = new Texture(Gdx.files.internal("jet1.png"));
        enemyShipTexture = new Texture(Gdx.files.internal("enemy_jet.png"));

        playerLaserTexture = new Texture(Gdx.files.internal("Shot_1.png"));
        enemyLaserTexture = new Texture(Gdx.files.internal("enemy_bullet.png"));

        playerShip = new Ship("LASER05",5,WORLD_WIDTH/2,WORLD_HEIGHT/4,50,50,playerShipTexture,playerLaserTexture);
        enemyShip = new Ship("LASER04",5,WORLD_WIDTH/2,WORLD_HEIGHT*3/4,50,50,enemyShipTexture,enemyLaserTexture);

        playerLasers = new LinkedList<>();
        enemyLasers = new LinkedList<>();


        batch = new SpriteBatch();

    }

    public void updateEnemyPosition(float x, float y) {
        float mirroredY = WORLD_HEIGHT - y - playerShip.getHeight(); // flip vertically
        enemyShip.setPosition(x, mirroredY);
    }


    private void handleInput(float delta) {
        float moveSpeed = 200 * delta;

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            playerShip.translateX(-moveSpeed);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            playerShip.translateX(moveSpeed);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
            playerShip.translateY(moveSpeed);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            playerShip.translateY(-moveSpeed);
        }

        // Fire laser
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            Laser[] newLasers = playerShip.fireLasers();

            for (Laser laser : newLasers) {
                playerLasers.add(laser);
            }
            enemyFire();

            client.sendMessage("SHOOT"); // 👈 send info to other player

        }
        float minY = 0;
        float maxY = WORLD_HEIGHT / 2 - playerShip.getHeight(); // bottom half only
        playerShip.setY(Math.max(minY, Math.min(playerShip.getY(), maxY)));
        float minX = 0;
        float maxX = WORLD_WIDTH;
        playerShip.setX(Math.max(minX, Math.min(playerShip.getX(),maxX)));

    }



    @Override
    public void render(float delta) {
        handleInput(delta);
        playerShip.update(delta);

        for (int i = 0; i < playerLasers.size(); i++) {
            Laser laser = playerLasers.get(i);
            laser.update(delta);
            if (laser.isOffScreen(WORLD_HEIGHT)) {
                playerLasers.remove(i);
                i--;
            }
        }

        for (int i = 0; i < enemyLasers.size(); i++) {
            Laser laser = enemyLasers.get(i);
            laser.update(delta);
            if (laser.isOffScreen(WORLD_HEIGHT)) {
                enemyLasers.remove(i);
                i--;
            }
        }

        float currentX = playerShip.getX();
        float currentY = playerShip.getY();

        if (currentX != lastSentX || currentY != lastSentY) {
            client.sendMessage("POS:" + currentX + "," + currentY);
            lastSentX = currentX;
            lastSentY = currentY;
        }

        batch.begin();

        batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        playerShip.draw(batch);
        enemyShip.draw(batch);
        for (Laser laser : playerLasers) {
            laser.draw(batch);
        }
        for (Laser laser : enemyLasers) {
            laser.draw(batch);
        }

        batch.end();

    }

    public void enemyFire() {
        Gdx.app.log("DEBUG", "enemyFire called");
        Laser[] newLasers = enemyShip.fireLasers();
        for (Laser laser : newLasers) {
            laser.setDirection(-1);
            // Redescendre le laser pour qu’il parte *sous* le vaisseau ennemi
            laser.setyPosition(enemyShip.getY() - laser.getHeight()) ;
            enemyLasers.add(laser);
        }
    }


    @Override
    public void resize(int width, int height) {

    }

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

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

}
