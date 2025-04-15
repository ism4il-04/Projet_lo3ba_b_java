package io.github.lo3ba.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class DirectedBullet extends Bullet {
    private final Vector2 direction;
    private final float speed;

    public DirectedBullet(Texture texture, float startX, float startY, float targetX, float targetY, float speed) {
        super(texture, startX, startY, speed);
        this.speed = speed;
        this.direction = new Vector2(targetX - startX, targetY - startY).nor();
    }

    @Override
    public void update(float delta) {
        if (!isActive()) return;
        translateX(direction.x * speed * delta);
        translateY(direction.y * speed * delta);
    }
}
