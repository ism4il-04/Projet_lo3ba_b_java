package io.github.lo3ba.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;

public class EnemyJet {
    private Texture texture;
    private Rectangle bounds;
    private float speed;

    public EnemyJet() {
        texture = new Texture("enemy_jet.png"); // Make sure this image is in your assets
        float x = MathUtils.random(0, Gdx.graphics.getWidth() - 64); // random X
        float y = Gdx.graphics.getHeight(); // start from top

        bounds = new Rectangle(x, y, 64, 64);
        speed = MathUtils.random(100, 250); // random speed between 100 and 250 pixels/sec
    }

    public void update(float delta) {
        bounds.y -= speed * delta; // Move down based on delta time
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public boolean isOffScreen() {
        return bounds.y + bounds.height < 0;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();

    }
}
