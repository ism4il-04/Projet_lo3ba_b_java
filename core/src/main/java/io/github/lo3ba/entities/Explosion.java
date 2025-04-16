package io.github.lo3ba.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class Explosion implements Disposable {
    private Texture texture;
    private final Rectangle bounds;
    private float timer;
    private final float duration = 0.5f;

    public Explosion(Texture texture, float x, float y) {
        this.texture = texture;
        this.bounds = new Rectangle(x, y, 50, 50);
        this.timer = 0;
    }

    public void update(float delta) {
        timer += delta;
    }

    public boolean isExpired() {
        return timer >= duration;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
