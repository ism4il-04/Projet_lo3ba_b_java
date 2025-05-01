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

public class BasicEnemy extends EnemyJet {
    private float fireCooldown = 0;
    private final float fireRate = 1.5f;
    private final float moveSpeed = 150f;
    private float directionChangeTimer = 0;
    private float directionX = 1;

    public BasicEnemy(Texture texture, Texture bulletTexture, Jet playerJet, GameScreen gameScreen) {
        this.sprite = new Sprite(texture);
        this.bullets = new ArrayList<>();
        this.bulletTexture = bulletTexture;
        this.playerJet = playerJet;
        this.gameScreen = gameScreen;

        this.sprite.setSize(60, 60);
        this.sprite.setPosition(
            MathUtils.random(0, Gdx.graphics.getWidth() - sprite.getWidth()),
            Gdx.graphics.getHeight()
        );
    }

    @Override
    public void update(float delta) {
        if (!isAlive) return;

        // Movement logic
        sprite.translateX(directionX * moveSpeed * delta);
        sprite.translateY(-moveSpeed * delta * 0.5f);

        if (sprite.getX() <= 0) {
            sprite.setX(0);
            directionX = 1;
        } else if (sprite.getX() >= Gdx.graphics.getWidth() - sprite.getWidth()) {
            sprite.setX(Gdx.graphics.getWidth() - sprite.getWidth());
            directionX = -1;
        }

        directionChangeTimer += delta;
        if (directionChangeTimer >= 2f) {
            directionX = MathUtils.randomBoolean() ? 1 : -1;
            directionChangeTimer = 0;
        }

        // Shooting logic
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
            200f
        ));
    }

    @Override
    public void die() {
        isAlive = false;
        if (MathUtils.random() < 0.2f) {
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
