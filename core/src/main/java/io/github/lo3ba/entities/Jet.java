package io.github.lo3ba.entities;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Jet implements Disposable {
    private final Sprite sprite;
    private final Array<Bullet> bullets;
    private final Texture bulletTexture;
    private float fireCooldown = 0;
    private final float moveSpeed = 300f;
    private final float fireRate = 0.2f;
    private final float screenWidth;
    private final float screenHeight;
    private boolean isShootingEnabled = true;
    private int health = 5; // l 2arwa7
    private final int maxHealth = 5;
    private boolean isInvulnerable = false;
    private float invulnerabilityTimer = 0;
    private final float invulnerabilityDuration = 1.5f;

    public Jet(Texture texture, Texture bulletTexture) {
        this.sprite = new Sprite(texture);
        this.sprite.setSize(60, 60);
        this.bullets = new Array<>();
        this.bulletTexture = bulletTexture;
        this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();

        setInitialPosition();
    }

    private void setInitialPosition() {
        this.sprite.setPosition(
            screenWidth/2f - sprite.getWidth()/2f,
            50f
        );
    }

    public void update(float delta) {
        handleMovement(delta);
        handleShooting(delta);
        updateBullets(delta);
        updateInvulnerability(delta);
    }

    private void updateInvulnerability(float delta) {
        if (isInvulnerable) {
            invulnerabilityTimer += delta;
            if (invulnerabilityTimer >= invulnerabilityDuration) {
                isInvulnerable = false;
                invulnerabilityTimer = 0;
            }
        }
    }

    private void handleMovement(float delta) {
        float velocity = moveSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) sprite.translateX(-velocity);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) sprite.translateX(velocity);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) sprite.translateY(velocity);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) sprite.translateY(-velocity);

        checkScreenBounds();
    }

    private void checkScreenBounds() {
        sprite.setX(MathUtils.clamp(sprite.getX(), 0, screenWidth - sprite.getWidth()));
        sprite.setY(MathUtils.clamp(sprite.getY(), 0, screenHeight - sprite.getHeight()));
    }

    private void handleShooting(float delta) {
        if (!isShootingEnabled) return;

        fireCooldown += delta;
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && fireCooldown >= fireRate) {
            fireBullet();
            fireCooldown = 0;
        }
    }

    private void fireBullet() {
        bullets.add(new Bullet(
            bulletTexture,
            sprite.getX() + sprite.getWidth()/2f - 5f,
            sprite.getY() + sprite.getHeight(),
            500f
        ));
    }

    private void updateBullets(float delta) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);
            if (bullet.isOffScreen()) {
                bullets.removeIndex(i).dispose();
            }
        }
    }

    public void takeDamage(int damage) {
        if (!isInvulnerable) {
            health = Math.max(0, health - damage);
            isInvulnerable = true;
            invulnerabilityTimer = 0;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!isInvulnerable || (isInvulnerable && ((int)(invulnerabilityTimer * 10) % 2) == 0)) {
            sprite.draw(batch);
        }

        for (Bullet bullet : bullets) {
            bullet.draw(batch);
        }
    }

    public void setShootingEnabled(boolean enabled) {
        this.isShootingEnabled = enabled;
    }

    @Override
    public void dispose() {
        for (Bullet bullet : bullets) {
            bullet.dispose();
        }
        bullets.clear();
    }

    public Sprite getSprite() { return sprite; }
    public Array<Bullet> getBullets() { return bullets; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isInvulnerable() { return isInvulnerable; }
}
