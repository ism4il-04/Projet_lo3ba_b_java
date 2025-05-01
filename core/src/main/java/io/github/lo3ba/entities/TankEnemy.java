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

public class TankEnemy extends EnemyJet {
    private float fireCooldown = 0;
    private final float fireRate = 2.5f;  // Slower shooting
    private final float moveSpeed = 80f;  // Slower movement
    private int health = 3;               // Takes 3 hits to destroy
    private float specialAttackTimer = 0;

    public TankEnemy(Texture texture, Texture bulletTexture, Jet playerJet, GameScreen gameScreen) {
        this.sprite = new Sprite(texture);
        this.bullets = new ArrayList<>();
        this.bulletTexture = bulletTexture;
        this.playerJet = playerJet;
        this.gameScreen = gameScreen;

        this.sprite.setSize(80, 80); // Larger sprite
        this.sprite.setPosition(
            MathUtils.random(0, Gdx.graphics.getWidth() - sprite.getWidth()),
            Gdx.graphics.getHeight()
        );
    }

    @Override
    public void update(float delta) {
        if (!isAlive) return;

        // Slow, steady movement
        sprite.translateY(-moveSpeed * delta);

        // Special attack pattern
        specialAttackTimer += delta;
        if (specialAttackTimer >= 5f) {
            fireTripleShot();
            specialAttackTimer = 0;
        }

        // Regular shooting
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
            150f  // Slower but more powerful bullets
        ));
    }

    private void fireTripleShot() {
        float startX = sprite.getX() + sprite.getWidth()/2f - 5f;
        float startY = sprite.getY();
        float targetX = playerJet.getSprite().getX() + playerJet.getSprite().getWidth()/2f;
        float targetY = playerJet.getSprite().getY() + playerJet.getSprite().getHeight()/2f;

        // Center shot
        bullets.add(new DirectedBullet(bulletTexture, startX, startY, targetX, targetY, 200f));

        // Left spread shot
        bullets.add(new DirectedBullet(bulletTexture, startX, startY,
            targetX - 100, targetY, 200f));

        // Right spread shot
        bullets.add(new DirectedBullet(bulletTexture, startX, startY,
            targetX + 100, targetY, 200f));
    }

    @Override
    public void die() {
        health--;
        if (health <= 0) {
            isAlive = false;
            if (MathUtils.random() < 0.5f) { // Very high chance for power-up
                PowerUp.Type[] types = PowerUp.Type.values();
                PowerUp.Type randomType = types[MathUtils.random(types.length - 1)]; // Can include BOMB
                gameScreen.spawnPowerUp(randomType, sprite.getX(), sprite.getY());
            }
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
