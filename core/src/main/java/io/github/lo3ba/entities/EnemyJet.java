package io.github.lo3ba.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import io.github.lo3ba.screens.GameScreen;

import java.util.List;

public abstract class EnemyJet implements Disposable {
    protected Sprite sprite;
    protected List<DirectedBullet> bullets;
    protected Texture bulletTexture;
    protected Jet playerJet;
    public boolean isAlive = true;
    protected GameScreen gameScreen;

    public abstract void update(float delta);

    public void render(SpriteBatch batch) {
        if (!isAlive) return;
        sprite.draw(batch);
        for (DirectedBullet bullet : bullets) {
            bullet.draw(batch);
        }
    }

    public Rectangle getBounds() {
        return sprite.getBoundingRectangle();
    }

    public boolean isOffScreen() {
        return sprite.getY() + sprite.getHeight() < 0;
    }

    public List<DirectedBullet> getBullets() {
        return bullets;
    }

    public abstract void die();

    @Override
    public abstract void dispose();
}
