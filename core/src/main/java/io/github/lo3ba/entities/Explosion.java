package io.github.lo3ba.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Explosion {
    private final Texture texture;
    private final Rectangle bounds;
    private float timer;
    private final float duration = 0.5f; // Explosion lasts for 1 second

    public Explosion(Texture texture, float x, float y) {
        this.texture = texture;
        this.bounds = new Rectangle(x, y, 64, 64); // Explosion size
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

    public void dispose() {
        texture.dispose();
    }
}
