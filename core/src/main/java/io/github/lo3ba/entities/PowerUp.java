package io.github.lo3ba.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class PowerUp extends Sprite {
    public enum Type { HEALTH, SHIELD, RAPID_FIRE, SPREAD_SHOT, BOMB }

    private Type type;
    private float duration;
    private boolean active = true;
    private float timeActive = 0;

    public PowerUp(Texture texture, Type type, float x, float y) {
        super(texture);
        this.type = type;
        this.duration = getDefaultDuration(type);
        setPosition(x, y);
        setSize(30, 30);
    }

    private float getDefaultDuration(Type type) {
        switch (type) {
            case SHIELD: return 5f;
            case RAPID_FIRE: return 10f;
            case SPREAD_SHOT: return 8f;
            default: return 0f;
        }
    }

    public void update(float delta) {
        if (!active) return;
        translateY(-100f * delta);
        timeActive += delta;
        if (timeActive > duration && duration > 0) {
            active = false;
        }
    }

    public Type getType() { return type; }
    public boolean isActive() { return active; }
    public Rectangle getBounds() { return new Rectangle(getX(), getY(), getWidth(), getHeight()); }
}
