package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;


public class Jet {
    private Sprite sprite;
    private float speed = 2f;
    private Array<Bullet> bullets;
    private float bulletCooldown = 0.2f;
    private float timeSinceLastShot = 0f;

    public Jet(Texture texture) {
        this.sprite = new Sprite(texture);
        sprite.setSize(1, 1);
        this.bullets = new Array<>();
    }
    public void shootBullet() {
        float jetX = sprite.getX() + sprite.getWidth() / 2 - 0.1f; // Center bullet on the jet
        float jetY = sprite.getY() + sprite.getHeight(); // Position bullet just above the jet
        Bullet newBullet = new Bullet(new Texture("shot_1.png"), jetX, jetY); // Create a new Bullet
        bullets.add(newBullet); // Add the new bullet to the bullets array
    }
    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            sprite.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            sprite.translateX(-speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            sprite.translateY(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            sprite.translateY(-speed * delta);
        }
        timeSinceLastShot += delta;
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && timeSinceLastShot >= bulletCooldown) {
            shootBullet(); // Create and add a bullet
            timeSinceLastShot = 0f; // Reset cooldown
        }

    }

    public void clampPosition(float worldWidth, float worldHeight) {
        float jetWidth = sprite.getWidth();
        float jetHeight = sprite.getHeight();
        sprite.setX(Math.max(0, Math.min(sprite.getX(), worldWidth - jetWidth)));
        sprite.setY(Math.max(0, Math.min(sprite.getY(), worldHeight - jetHeight)));
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
        for (Bullet bullet : bullets) {
            bullet.draw(batch);
        }
    }
    public Array<Bullet> getBullets() {
        return bullets;
    }
    public Sprite getSprite() {
        return sprite;
    }
}
