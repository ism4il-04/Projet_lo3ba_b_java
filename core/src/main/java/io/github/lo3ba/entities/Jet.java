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
    private float currentFireRate = 0.2f;
    private boolean hasShield = false;
    private float powerUpTimer = 0;
    private PowerUp.Type activePowerUp = null;
    private int bulletCount = 1;

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
        updatePowerUps(delta);
    }
    private void updatePowerUps(float delta) {
        if (activePowerUp != null) {
            powerUpTimer += delta;
            if (powerUpTimer >= getPowerUpDuration(activePowerUp)) {
                resetPowerUp();
            }
        }
    }
    private float getPowerUpDuration(PowerUp.Type type) {
        switch (type) {
            case SHIELD: return 5f;
            case RAPID_FIRE: return 10f;
            case SPREAD_SHOT: return 8f;
            default: return 0f;
        }
    }

    private void resetPowerUp() {
        currentFireRate = 0.2f;
        bulletCount = 1;
        setInvulnerable(false);
        activePowerUp = null;
        powerUpTimer = 0;
    }
    public void applyPowerUp(PowerUp.Type type) {
        activePowerUp = type;
        powerUpTimer = 0;

        switch (type) {
            case HEALTH:
                heal(1);
                break;
            case SHIELD:
                setInvulnerable(true);
                break;
            case RAPID_FIRE:
                currentFireRate = 0.1f;
                break;
            case SPREAD_SHOT:
                bulletCount = 3;
                break;
            case BOMB:
                // Géré dans GameScreen
                break;
        }
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
        if (bulletCount == 1) {
            bullets.add(new Bullet(
                bulletTexture,
                sprite.getX() + sprite.getWidth()/2f - 5f,
                sprite.getY() + sprite.getHeight(),
                500f
            ));
        } else {
            // Spread shot
            for (int i = 0; i < bulletCount; i++) {
                float offset = (i - (bulletCount-1)/2f) * 15f;
                bullets.add(new Bullet(
                    bulletTexture,
                    sprite.getX() + sprite.getWidth()/2f - 5f + offset,
                    sprite.getY() + sprite.getHeight(),
                    500f
                ));
            }
        }
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
    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
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
    public void setInvulnerable(boolean invulnerable) {
        this.isInvulnerable = invulnerable;
        if (!invulnerable) invulnerabilityTimer = 0;
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
