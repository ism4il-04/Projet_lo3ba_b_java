package io.github.lo3ba.entities;

import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import io.github.lo3ba.screens.GameScreen;
import java.util.List;
public class FastEnemy extends EnemyJet {
    private float fireCooldown = 0;
    private final float fireRate = 0.8f;  // Faster shooting
    private final float moveSpeed = 250f; // Faster movement
    private float directionX = MathUtils.randomSign();
    private float zigzagTimer = 0;

    public FastEnemy(Texture texture, Texture bulletTexture, Jet playerJet, GameScreen gameScreen) {
        this.sprite = new Sprite(texture);
        this.bullets = new ArrayList<>();
        this.bulletTexture = bulletTexture;
        this.playerJet = playerJet;
        this.gameScreen = gameScreen;

        this.sprite.setSize(50, 50); // Slightly smaller
        this.sprite.setPosition(
            MathUtils.random(0, Gdx.graphics.getWidth() - sprite.getWidth()),
            Gdx.graphics.getHeight()
        );
    }

    @Override
    public void update(float delta) {
        if (!isAlive) return;

        // Zigzag movement pattern
        zigzagTimer += delta;
        if (zigzagTimer >= 0.5f) {
            directionX *= -1;
            zigzagTimer = 0;
        }

        sprite.translateX(directionX * moveSpeed * delta * 1.2f); // Horizontal movement
        sprite.translateY(-moveSpeed * delta * 0.8f); // Vertical movement

        // Screen bounds check
        if (sprite.getX() <= 0) {
            sprite.setX(0);
            directionX = 1;
        } else if (sprite.getX() >= Gdx.graphics.getWidth() - sprite.getWidth()) {
            sprite.setX(Gdx.graphics.getWidth() - sprite.getWidth());
            directionX = -1;
        }

        // Rapid firing
        fireCooldown += delta;
        if (fireCooldown >= fireRate && playerJet != null) {
            fireBullet();
            fireCooldown = 0;
        }

        // Update bullets
        bullets.removeIf(bullet -> {
            bullet.update(delta);
            if (bullet.isOffScreen()) {
                bullet.dispose();
                return true;
            }
            return false;
        });
    }

    private void fireBullet() {
        float startX = sprite.getX() + sprite.getWidth()/2f - 5f;
        float startY = sprite.getY();
        float targetX = playerJet.getSprite().getX() + playerJet.getSprite().getWidth()/2f;
        float targetY = playerJet.getSprite().getY() + playerJet.getSprite().getHeight()/2f;

        bullets.add(new DirectedBullet(
            bulletTexture,
            startX, startY,
            targetX, targetY,
            300f  // Faster bullets
        ));
    }

    @Override
    public void die() {
        isAlive = false;
        if (MathUtils.random() < 0.3f) { // Higher chance for power-up
            PowerUp.Type[] types = PowerUp.Type.values();
            PowerUp.Type randomType = types[MathUtils.random(0, types.length - 2)];
            gameScreen.spawnPowerUp(randomType, sprite.getX(), sprite.getY());
        }
    }

    @Override
    public void dispose() {
        for (DirectedBullet bullet : bullets) {
            bullet.dispose();
        }
        bullets.clear();
    }
}
