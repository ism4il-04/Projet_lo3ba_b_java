package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
public class Bullet extends Sprite {
    public float speed = 5f;

    public Bullet(Texture texture, float x, float y) {
        super(texture);
        this.setSize(0.2f, 0.5f);
        this.setPosition(x, y);
    }

    public void update(float delta) {
        this.translateY(speed * delta);
    }
}
