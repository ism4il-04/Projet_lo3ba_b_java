package io.github.lo3ba.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class Bullet extends Sprite implements Disposable {
    public final float speed;
    private boolean active = true;

    public Bullet(Texture texture, float x, float y, float speed) {
        super(texture);
        setPosition(x - getWidth()/2f, y);
        setSize(10, 20);
        this.speed = speed;
    }

    public void update(float delta) {
        if (!active) return;
        translateY(speed * delta);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public boolean isOffScreen() {
        return getY() > Gdx.graphics.getHeight() || getY() + getHeight() < 0;
    }

    @Override
    public void dispose() {

    }
}
