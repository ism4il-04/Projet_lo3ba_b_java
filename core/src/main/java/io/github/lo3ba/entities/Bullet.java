package io.github.lo3ba.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Bullet extends Sprite {
    private final float speed;

    public Bullet(Texture texture, float x, float y, float speed) {
        super(texture);
        setPosition(x, y);
        setSize(10, 20);
        this.speed = speed;
    }

    public void update(float delta) {
        translateY(speed * delta);
    }

    public float getY() {
        return super.getY();
    }
}
