package io.github.lo3ba.entities;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;

public class Jet {
    private final Sprite sprite;
    private final Array<Bullet> bullets;
    private final Texture bulletTexture;
    private float fireCooldown = 0;
    private final float moveSpeed = 300f;
    private final float fireRate = 0.2f;

    public Jet(Texture texture) {
        this.sprite = new Sprite(texture);
        this.sprite.setSize(60, 60);
        this.bullets = new Array<>();
        this.bulletTexture = new Texture("shot_1.png");
    }

    public void update(float delta) {
        // Movement
        float velocity = moveSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) sprite.translateX(-velocity);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) sprite.translateX(velocity);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) sprite.translateY(velocity);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) sprite.translateY(-velocity);

        // Shooting
        fireCooldown += delta;
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && fireCooldown >= fireRate) {
            bullets.add(new Bullet(
                bulletTexture,
                sprite.getX() + sprite.getWidth()/2 - 5,
                sprite.getY() + sprite.getHeight(),
                500f
            ));
            fireCooldown = 0;
        }

        // Update bullets
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update(delta);
            if (b.getY() > Gdx.graphics.getHeight()) {
                bullets.removeIndex(i);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
        for (Bullet bullet : bullets) {
            bullet.draw(batch);
        }
    }

    public Sprite getSprite() { return sprite; }
}
