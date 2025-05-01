package io.github.lo3ba.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Laser  {

    private float movementSpeed;

    float xPosition, yPosition; //bottom center
    float width, height;


    //graphics
    Texture laserTexture;

    public Laser(float movementSpeed, float xPosition, float yPosition, float width, float height, Texture laserTexture) {
        this.movementSpeed = movementSpeed;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
        this.laserTexture = laserTexture;
    }

    public void update(float delta) {
        yPosition += 200 * delta; // laser speed
    }

    public boolean isOffScreen(float screenHeight) {
        return yPosition > screenHeight;
    }

    public void draw (Batch batch) {
        batch.draw(laserTexture, xPosition, yPosition, width, height);
    }
}
