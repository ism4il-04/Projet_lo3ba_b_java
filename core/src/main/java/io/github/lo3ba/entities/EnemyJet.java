package io.github.lo3ba.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;
import java.util.List;

public class EnemyJet implements Disposable {
    private final Sprite sprite;
    private final List<DirectedBullet> bullets;
    private final Texture bulletTexture;
    private float fireCooldown = 0;
    private final float fireRate = 1.5f;
    private final float moveSpeed = 150f;
    private float directionChangeTimer = 0;
    private float directionX = 1;
    private final Jet playerJet;

    public EnemyJet(Texture texture, Texture bulletTexture, Jet playerJet) {
        this.sprite = new Sprite(texture);
        this.sprite.setSize(60, 60);
        this.bullets = new ArrayList<>();
        this.bulletTexture = bulletTexture;
        this.playerJet = playerJet;


        this.sprite.setPosition(
            MathUtils.random(0, Gdx.graphics.getWidth() - sprite.getWidth()),
            Gdx.graphics.getHeight()
        );
    }

    public void update(float delta) {
        // Mouvement
        sprite.translateX(directionX * moveSpeed * delta);
        sprite.translateY(-moveSpeed * delta * 0.5f);

        // bach maykhrjech mn l ecran
        if (sprite.getX() <= 0) {
            sprite.setX(0);
            directionX = 1;
        } else if (sprite.getX() >= Gdx.graphics.getWidth() - sprite.getWidth()) {
            sprite.setX(Gdx.graphics.getWidth() - sprite.getWidth());
            directionX = -1;
        }

        // mouvement aleatoire
        directionChangeTimer += delta;
        if (directionChangeTimer >= 2f) {
            directionX = MathUtils.randomBoolean() ? 1 : -1;
            directionChangeTimer = 0;
        }

        // Tir
        fireCooldown += delta;
        if (fireCooldown >= fireRate && playerJet != null) {
            fireBullet();
            fireCooldown = 0;
        }


        for (int i = bullets.size() - 1; i >= 0; i--) {
            DirectedBullet bullet = bullets.get(i);
            bullet.update(delta);
            if (bullet.isOffScreen()) {
                bullets.remove(i).dispose();
            }
        }
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

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
        for (DirectedBullet bullet : bullets) {
            bullet.draw(batch);
        }
    }

    public boolean isOffScreen() {
        return sprite.getY() + sprite.getHeight() < 0;
    }

    public Rectangle getBounds() {
        return new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    public List<DirectedBullet> getBullets() {
        return bullets;
    }

    @Override
    public void dispose() {
        for (DirectedBullet bullet : bullets) {
            bullet.dispose();
        }
        bullets.clear();
    }
}
